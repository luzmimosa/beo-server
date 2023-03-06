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

    private final HashMap<Class<? extends Listener>, ArrayList<Method>> REGISTERED_LISTENERS;

    public EventManager() {
        this.REGISTERED_LISTENERS = new HashMap<>();
    }

    public void addEventListener(Class<? extends Listener> listenerClass) {
        if (this.REGISTERED_LISTENERS.containsKey(listenerClass)) return;
    }

    public void removeEventListener(Class<? extends Listener> listenerClass) {
        this.REGISTERED_LISTENERS.remove(listenerClass);
    }

    private List<Method> getValidMethods(Class<? extends Listener> listenerClass) {
        List<Method> validMethods = new ArrayList<>();
        for (Method method : listenerClass.getMethods()) {
            Class<Event> eventClass = getEventTarget(method);
            if (eventClass != null) {
                validMethods.add(method);
            }
        }
        return validMethods;
    }

    private @Nullable Class<Event> getEventTarget(Method method) {
        EventListener annotation = method.getAnnotation(EventListener.class);
        if (annotation == null) return null;

        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 1) return null;

        Class<?> parameterType = parameterTypes[0];

        if (!Event.class.isAssignableFrom(parameterType)) return null;

        try {
            Class<Event> eventClass = (Class<Event>) parameterType;
            return eventClass;
        } catch (ClassCastException e) {
            return null;
        }
    }

}
