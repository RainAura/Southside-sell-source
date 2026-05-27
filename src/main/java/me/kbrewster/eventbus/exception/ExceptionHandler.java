package me.kbrewster.eventbus.exception;

@FunctionalInterface
public interface ExceptionHandler {
    void handle(Exception exception);
}
