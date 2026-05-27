package com.mumfrey.liteloader.common;

public abstract class LoadingProgress {
    private static LoadingProgress instance;

    protected LoadingProgress() {
        instance = this;
    }

    public static void setEnabled(boolean enabled) {
        if (instance != null) {
            instance._setEnabled(enabled);
        }
    }

    public static void dispose() {
        if (instance != null) {
            instance._dispose();
        }
    }

    public static void incLiteLoaderProgress() {
        if (instance != null) {
            instance._incLiteLoaderProgress();
        }
    }

    public static void setMessage(String format, String ... args) {
        if (instance != null) {
            instance._setMessage(String.format(format, args));
        }
    }

    public static void setMessage(String message) {
        if (instance != null) {
            instance._setMessage(message);
        }
    }

    public static void incLiteLoaderProgress(String format, String ... args) {
        if (instance != null) {
            instance._incLiteLoaderProgress(String.format(format, args));
        }
    }

    public static void incLiteLoaderProgress(String message) {
        if (instance != null) {
            instance._incLiteLoaderProgress(message);
        }
    }

    public static void incTotalLiteLoaderProgress(int by) {
        if (instance != null) {
            instance._incTotalLiteLoaderProgress(by);
        }
    }

    protected abstract void _setEnabled(boolean var1);

    protected abstract void _dispose();

    protected abstract void _incLiteLoaderProgress();

    protected abstract void _setMessage(String var1);

    protected abstract void _incLiteLoaderProgress(String var1);

    protected abstract void _incTotalLiteLoaderProgress(int var1);
}
