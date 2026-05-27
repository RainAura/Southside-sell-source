package me.kbrewster.eventbus;

import me.kbrewster.eventbus.collection.ConcurrentSubscriberArrayList;
import me.kbrewster.eventbus.collection.SubscriberArrayList;
import me.kbrewster.eventbus.exception.ExceptionHandler;
import me.kbrewster.eventbus.invokers.InvokerType;
import me.kbrewster.eventbus.invokers.ReflectionInvoker;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.function.Supplier;

public class EventBus {

    public static class Subscriber {
        public final Object obj;
        public final int priority;
        public final InvokerType.SubscriberMethod invoker;

        Subscriber(Object obj, int priority, InvokerType.SubscriberMethod invoker) {
            this.obj = obj;
            this.priority = priority;
            this.invoker = invoker;
        }

        void invoke(Object arg) throws Exception {
            invoker.invoke(arg);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            Subscriber that = (Subscriber) other;
            return obj.equals(that.obj);
        }

        @Override
        public int hashCode() {
            return obj.hashCode();
        }
    }

    private final AbstractMap<Class<?>, List<Subscriber>> subscribers;
    private final InvokerType invokerType;
    private final ExceptionHandler exceptionHandler;
    private final boolean threadSafety;

    public EventBus() {
        this(new ReflectionInvoker(), new ExceptionHandler() {
            @Override
            public void handle(Exception exception) {
                throw new RuntimeException(exception);
            }
        }, true);
    }

    public EventBus(InvokerType invokerType, ExceptionHandler exceptionHandler) {
        this(invokerType,exceptionHandler,true);
    }

    public EventBus(InvokerType invokerType, ExceptionHandler exceptionHandler, boolean threadSafety) {
        this.invokerType = invokerType;
        this.exceptionHandler = exceptionHandler;
        this.threadSafety = threadSafety;
        this.subscribers = threadSafety ? new ConcurrentHashMap<>() : new HashMap<>();
    }

    public void register(Object obj) {
        try {
            for (Method method : obj.getClass().getDeclaredMethods()) {
                Subscribe sub = method.getAnnotation(Subscribe.class);
                if (sub == null) continue;

                Class<?> parameterClazz = method.getParameterTypes()[0];
                if (method.getParameterCount() != 1)
                    throw new IllegalArgumentException("Subscribed method must only have one parameter.");
                if (method.getReturnType() != Void.TYPE)
                    throw new IllegalArgumentException("Subscribed method must be of type 'Void'. ");
                if (parameterClazz.isPrimitive())
                    throw new IllegalArgumentException("Cannot subscribe method to a primitive.");
                if ((parameterClazz.getModifiers() & (Modifier.ABSTRACT | Modifier.INTERFACE)) != 0)
                    throw new IllegalArgumentException("Cannot subscribe method to a polymorphic class.");

                InvokerType.SubscriberMethod subscriberMethod = invokerType.setup(obj, obj.getClass(), parameterClazz, method);
                Subscriber subscriber = new Subscriber(obj, sub.priority(), subscriberMethod);
                subscribers.putIfAbsent(parameterClazz, threadSafety ? new ConcurrentSubscriberArrayList() : new SubscriberArrayList());
                subscribers.get(parameterClazz).add(subscriber);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void unregister(Object obj) {
        for (Method method : obj.getClass().getDeclaredMethods()) {
            if (method.getAnnotation(Subscribe.class) == null) {
                continue;
            }
            List<Subscriber> eventSubscribers = subscribers.get(method.getParameterTypes()[0]);
            if (eventSubscribers != null) {
                eventSubscribers.remove(new Subscriber(obj, -1, null));
            }
        }
    }

    public void post(Object event) {
        List<Subscriber> events = subscribers.get(event.getClass());
        if (events != null) {
            for (int i = events.size() - 1; i >= 0; i--) {
                try {
                    events.get(i).invoke(event);
                } catch (Exception e) {
                    exceptionHandler.handle(e);
                }
            }
        }
    }

    public <T> void post(Class<T> clazz, Supplier<T> supplier) {
        List<Subscriber> events = subscribers.get(clazz);
        if (events != null) {
            T event = supplier.get();
            for (int i = events.size() - 1; i >= 0; i--) {
                try {
                    events.get(i).invoke(event);
                } catch (Exception e) {
                    exceptionHandler.handle(e);
                }
            }
        }
    }

    public List<Subscriber> getSubscribedEvents(Class<?> clazz) {
        return subscribers.get(clazz);
    }

    private void iterateSubclasses(Object obj, java.util.function.Consumer<Class<?>> body) {
        Class<?> postClazz = obj.getClass();
        do {
            body.accept(postClazz);
            postClazz = postClazz.getSuperclass();
        } while (postClazz != null);
    }
}
