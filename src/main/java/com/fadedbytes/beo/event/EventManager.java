package com.fadedbytes.beo.event;

import com.fadedbytes.beo.event.listener.EventListener;
import com.fadedbytes.beo.event.listener.Listener;
import com.fadedbytes.beo.event.type.Event;
import com.fadedbytes.beo.log.BeoLogger;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EventManager {

    public static final ThreadGroup eventThreadGroup = new ThreadGroup("Event Thread Group");

    private final ArrayList<RegisteredListener> REGISTERED_LISTENERS;
    private final ConcurrentLinkedQueue<Event> EVENT_QUEUE;
    private final Thread EVENT_LAUNCHER_THREAD;

    private final BeoLogger logger;
    private boolean isRunning;

    public EventManager(BeoLogger logger) {
        this.logger = logger;

        this.REGISTERED_LISTENERS = new ArrayList<>();
        this.EVENT_QUEUE = new ConcurrentLinkedQueue<>();

        this.isRunning = true;
        this.EVENT_LAUNCHER_THREAD = new Thread(eventThreadGroup, this::listenEventLaunches, "Event Manager");
        this.EVENT_LAUNCHER_THREAD.start();
    }

    public synchronized void addEventListener(Class<? extends Listener> listenerClass) throws IllegalArgumentException {
        if (this.containsListener(listenerClass)) {
            throw new IllegalArgumentException("Listener class already registered: " + listenerClass.getName());
        }

        RegisteredListener registeredListener = new RegisteredListener(listenerClass);
        this.logger.info("Registered listener: " + registeredListener.getListenerClass().getName());
        this.REGISTERED_LISTENERS.add(registeredListener);
    }

    public boolean containsListener(Class<? extends Listener> listenerClass) {
        for (RegisteredListener registeredListener : this.REGISTERED_LISTENERS) {
            if (registeredListener.getListenerClass().equals(listenerClass)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void removeEventListener(Class<? extends Listener> listenerClass) {
        this.REGISTERED_LISTENERS.removeAll(
                this.REGISTERED_LISTENERS.stream()
                        .filter(registeredListener -> registeredListener.getListenerClass().equals(listenerClass))
                        .toList()
        );
    }

    public void launchEvent(Event event) {
        if (!Thread.currentThread().equals(this.EVENT_LAUNCHER_THREAD)) {
            this.EVENT_QUEUE.add(event);
            return;
        }

        this.notifyListeners(event);
        event.onLifecycleEnd();
    }

    private void notifyListeners(Event event) {
        for (RegisteredListener registeredListener : this.REGISTERED_LISTENERS) {
            registeredListener.getValidMethods().entrySet().stream()
                    .filter(entry -> entry.getKey().isAssignableFrom(event.getClass()))
                    .forEach(entry -> {
                        try {
                            Method[] methods = entry.getValue();
                            for (Method method : methods) {
                                method.invoke(null, event);
                            }
                        } catch (Exception e) {
                            this.logger.error("Failed to invoke event listener method: " + e.getMessage(), e.getStackTrace());
                        }
                    });
        }
    }

    private void listenEventLaunches() {
        while (this.isRunning) {
            Event event = this.EVENT_QUEUE.poll();

            if (event != null) {
                launchEvent(event);
            } else {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class RegisteredListener {
        private final Class<? extends Listener> listenerClass;
        private final HashMap<Class<Event>, Method[]> validMethods;

        public RegisteredListener(Class<? extends Listener> listenerClass) {
            this.listenerClass = listenerClass;
            this.validMethods = getValidMethods(listenerClass);

            if (this.validMethods.isEmpty()) {
                throw new IllegalArgumentException("Listener class has no valid methods: " + listenerClass.getName());
            }
        }

        public Class<? extends Listener> getListenerClass() {
            return listenerClass;
        }

        public HashMap<Class<Event>, Method[]> getValidMethods() {
            return new HashMap<>(validMethods);
        }

        private static HashMap<Class<Event>, Method[]> getValidMethods(Class<? extends Listener> listenerClass) {
            HashMap<Class<Event>, Method[]> validMethods = new HashMap<>();

            for (Method method : listenerClass.getMethods()) {
                Class<Event> eventClass = getEventTarget(method);
                if (eventClass == null) continue;

                if (!validMethods.containsKey(eventClass)) {
                    validMethods.put(eventClass, new Method[] {method});
                } else {
                    List<Method> methods = new ArrayList<>(List.of(validMethods.get(eventClass)));
                    methods.add(method);
                    validMethods.put(eventClass, methods.toArray(new Method[0]));
                }
            }
            return validMethods;
        }

        private static @Nullable Class<Event> getEventTarget(Method method) {
            EventListener annotation = method.getAnnotation(EventListener.class);
            if (annotation == null) return null;

            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 1) return null;

            Class<?> parameterType = parameterTypes[0];

            if (!Event.class.isAssignableFrom(parameterType)) return null;

            try {
                return (Class<Event>) parameterType;
            } catch (ClassCastException e) {
                return null;
            }
        }
    }

}
