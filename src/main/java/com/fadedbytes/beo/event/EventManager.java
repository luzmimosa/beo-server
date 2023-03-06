package com.fadedbytes.beo.event;

import com.fadedbytes.beo.event.listener.EventListener;
import com.fadedbytes.beo.event.listener.Listener;
import com.fadedbytes.beo.event.type.Event;
import com.fadedbytes.beo.log.LogManager;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventManager {

    private final ArrayList<RegisteredListener> REGISTERED_LISTENERS;

    public EventManager() {
        this.REGISTERED_LISTENERS = new ArrayList<>();
    }

    public synchronized void addEventListener(Class<? extends Listener> listenerClass) throws IllegalArgumentException {
        if (this.containsListener(listenerClass)) {
            throw new IllegalArgumentException("Listener class already registered: " + listenerClass.getName());
        }

        RegisteredListener registeredListener = new RegisteredListener(listenerClass);
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
                    validMethods.put(eventClass, new Method[1]);
                } else {
                    Method[] methods = validMethods.get(eventClass);
                    Method[] newMethods = new Method[methods.length + 1];
                    System.arraycopy(methods, 0, newMethods, 0, methods.length);
                    methods = newMethods;
                    validMethods.put(eventClass, methods);
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
