package me.eigenraven.lwjgl3ify.core;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Config {

    public static boolean INPUT_RAW_MOUSE = false;
    public static boolean OPENAL_ENABLE_HRTF = false;
    public static boolean DEBUG_PRINT_KEY_EVENTS = false;
    public static boolean DEBUG_PRINT_MOUSE_EVENTS = false;
    public static boolean DEBUG_REGISTER_OPENGL_LOGGER = false;
    public static boolean WINDOW_START_MAXIMIZED = false, WINDOW_START_FOCUSED = true, WINDOW_START_ICONIFIED = false;
    public static boolean WINDOW_DECORATED = true;
    public static boolean OPENGL_DEBUG_CONTEXT = false;
    public static boolean OPENGL_SRGB_CONTEXT = false;
    public static boolean OPENGL_DOUBLEBUFFER = true;
    public static boolean OPENGL_CONTEXT_NO_ERROR = false;

    public static boolean INPUT_INVERT_WHEEL = false;
    public static boolean INPUT_INVERT_X_WHEEL = false;
    public static double INPUT_SCROLL_SPEED = 1.0;
    public static boolean INPUT_CTRL_ALT_TEXT = false;
    public static boolean INPUT_ALTGR_ESCAPE_CODES = false;

    public static String X11_CLASS_NAME = "minecraft";
    public static String COCOA_FRAME_NAME = "minecraft";
}
