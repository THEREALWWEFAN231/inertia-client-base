package com.inertiaclient.base.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

//"based" on darkmagician6 eventapi, but nothing is copied so... just the ideas, also darkmagician6 used CopyOnWriteArrayList its not *really* needed. We shouldn't be using the EventManager on other threads and I "fixed" unregister & call
//this is actually significantly faster than darkmagician6s partially because of CopyOnWriteArrayList, and my slight "improvements", of course darkmagician6s is still really fast, but from my tests this is like 10x faster(at times), on register, and unregister(I didn't *really* test call too much)?!?!?
public class EventManager {

    //a map of all registered event types and the methods they invoke when being called
    private static final Map<Class<? extends Event>, CopyOnWriteArrayList<InvokeWrapper>> REGISTRY_MAP = new HashMap<>();

    public static void register(Object object) {
        Class<?> classToRegister = null;
        //static
        if (object instanceof Class<?> c) {
            classToRegister = c;
        } else {
            classToRegister = object.getClass();
        }

        for (Field field : classToRegister.getDeclaredFields()) {
            tryRegisterField(object, field);
        }
    }

    public static void unregister(Object object) {

        Iterator<Map.Entry<Class<? extends Event>, CopyOnWriteArrayList<InvokeWrapper>>> iterator = REGISTRY_MAP.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Class<? extends Event>, CopyOnWriteArrayList<InvokeWrapper>> entry = iterator.next();

            CopyOnWriteArrayList<InvokeWrapper> arrayList = entry.getValue();
            for (int i = 0; i < arrayList.size(); i++) {
                InvokeWrapper methodInvokeType = arrayList.get(i);

                if (methodInvokeType.getRegisteredFrom() == object) {
                    arrayList.remove(i);
                    i--;
                }
            }
            if (arrayList.isEmpty()) {
                iterator.remove();
            }
        }

    }

    public static void fire(Event event) {
        var eventsToInvoke = REGISTRY_MAP.get(event.getClass());
        if (eventsToInvoke == null) {
            return;
        }

        //prevents a concurrent exception, if we unregister the/a event in the @EventTarget method
        // eventsToInvoke = (ArrayList<MethodInvokeType>) eventsToInvoke.clone();
        for (InvokeWrapper methodInvokeType : eventsToInvoke) {
            try {
                methodInvokeType.eventListener.handle(event);
            } catch (Throwable e) {
                System.out.println("Failed to invoke event " + event.getClass() + "+" + methodInvokeType.eventListener);
                e.printStackTrace();
            }
        }
    }

    public static void tryRegisterField(Object objectInstanceWithField, Field field) {
        if (field.getType().equals(EventListener.class) && field.isAnnotationPresent(EventTarget.class)) {
            try {
                //if (!field.canAccess(objectInstanceWithField)) {
                field.setAccessible(true);
                //}
                Class<? extends Event> eventClass = null;

                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType type) {
                    var arguments = type.getActualTypeArguments();
                    if (arguments.length == 1) {
                        eventClass = (Class<? extends Event>) arguments[0];
                    }
                }
                if (eventClass == null) {
                    return;
                }

                CopyOnWriteArrayList<InvokeWrapper> arrayList = REGISTRY_MAP.get(eventClass);
                if (arrayList == null) {
                    REGISTRY_MAP.put(eventClass, arrayList = new CopyOnWriteArrayList<>());
                }


                arrayList.add(new InvokeWrapper(objectInstanceWithField, (EventListener<?>) field.get(objectInstanceWithField)));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Getter
    @AllArgsConstructor
    private static class InvokeWrapper {

        private final Object registeredFrom;
        private final EventListener eventListener;

    }

}
