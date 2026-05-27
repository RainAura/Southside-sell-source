package me.kbrewster.eventbus;

import me.kbrewster.eventbus.exception.ExceptionHandler;
import me.kbrewster.eventbus.invokers.InvokerType;
import me.kbrewster.eventbus.invokers.ReflectionInvoker;

import java.util.function.Supplier;

public class EventBusUtil {

    public static EventBus eventbus(java.util.function.Consumer<EventBusBuilder> lambda) {
        EventBusBuilder builder = new EventBusBuilder();
        lambda.accept(builder);
        return builder.build();
    }

    public static class EventBusBuilder {

        private InvokerType invokerType = new ReflectionInvoker();
        private ExceptionHandler exceptionHandler = new ExceptionHandler() {
            @Override
            public void handle(Exception exception) {
                throw new RuntimeException(exception);
            }
        };
        private boolean threadSafety = false;

        public void invoker(Supplier<InvokerType> lambda) {
            this.invokerType = lambda.get();
        }

        public void threadSafety(Supplier<Boolean> lambda) {
            this.threadSafety = lambda.get();
        }

        public void exceptionHandler(java.util.function.Consumer<Exception> lambda) {
            this.exceptionHandler = new ExceptionHandler() {
                @Override
                public void handle(Exception exception) {
                    lambda.accept(exception);
                }
            };
        }

        public EventBus build() {
            return new EventBus(this.invokerType, this.exceptionHandler, this.threadSafety);
        }
    }
}
