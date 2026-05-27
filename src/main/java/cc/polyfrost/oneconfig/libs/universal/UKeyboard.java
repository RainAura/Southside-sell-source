package cc.polyfrost.oneconfig.libs.universal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.*;
import org.lwjglx.input.Keyboard;
import org.lwjglx.input.Mouse;

public final class UKeyboard
{
    public static final UKeyboard INSTANCE;
    public static final int KEY_NONE;
    public static final int KEY_ESCAPE;
    public static final int KEY_LMETA;
    public static final int KEY_RMETA;
    public static final int KEY_LCONTROL;
    public static final int KEY_RCONTROL;
    public static final int KEY_LSHIFT;
    public static final int KEY_RSHIFT;
    public static final int KEY_LMENU;
    public static final int KEY_RMENU;
    public static final int KEY_MINUS;
    public static final int KEY_EQUALS;
    public static final int KEY_BACKSPACE;
    public static final int KEY_ENTER;
    public static final int KEY_TAB;
    public static final int KEY_LBRACKET;
    public static final int KEY_RBRACKET;
    public static final int KEY_SEMICOLON;
    public static final int KEY_APOSTROPHE;
    public static final int KEY_GRAVE;
    public static final int KEY_BACKSLASH;
    public static final int KEY_COMMA;
    public static final int KEY_PERIOD;
    public static final int KEY_SLASH;
    public static final int KEY_MULTIPLY;
    public static final int KEY_SPACE;
    public static final int KEY_CAPITAL;
    public static final int KEY_LEFT;
    public static final int KEY_UP;
    public static final int KEY_RIGHT;
    public static final int KEY_DOWN;
    public static final int KEY_NUMLOCK;
    public static final int KEY_SCROLL;
    public static final int KEY_SUBTRACT;
    public static final int KEY_ADD;
    public static final int KEY_DIVIDE;
    public static final int KEY_DECIMAL;
    public static final int KEY_NUMPAD0;
    public static final int KEY_NUMPAD1;
    public static final int KEY_NUMPAD2;
    public static final int KEY_NUMPAD3;
    public static final int KEY_NUMPAD4;
    public static final int KEY_NUMPAD5;
    public static final int KEY_NUMPAD6;
    public static final int KEY_NUMPAD7;
    public static final int KEY_NUMPAD8;
    public static final int KEY_NUMPAD9;
    public static final int KEY_A;
    public static final int KEY_B;
    public static final int KEY_C;
    public static final int KEY_D;
    public static final int KEY_E;
    public static final int KEY_F;
    public static final int KEY_G;
    public static final int KEY_H;
    public static final int KEY_I;
    public static final int KEY_J;
    public static final int KEY_K;
    public static final int KEY_L;
    public static final int KEY_M;
    public static final int KEY_N;
    public static final int KEY_O;
    public static final int KEY_P;
    public static final int KEY_Q;
    public static final int KEY_R;
    public static final int KEY_S;
    public static final int KEY_T;
    public static final int KEY_U;
    public static final int KEY_V;
    public static final int KEY_W;
    public static final int KEY_X;
    public static final int KEY_Y;
    public static final int KEY_Z;
    public static final int KEY_0;
    public static final int KEY_1;
    public static final int KEY_2;
    public static final int KEY_3;
    public static final int KEY_4;
    public static final int KEY_5;
    public static final int KEY_6;
    public static final int KEY_7;
    public static final int KEY_8;
    public static final int KEY_9;
    public static final int KEY_F1;
    public static final int KEY_F2;
    public static final int KEY_F3;
    public static final int KEY_F4;
    public static final int KEY_F5;
    public static final int KEY_F6;
    public static final int KEY_F7;
    public static final int KEY_F8;
    public static final int KEY_F9;
    public static final int KEY_F10;
    public static final int KEY_F11;
    public static final int KEY_F12;
    public static final int KEY_F13;
    public static final int KEY_F14;
    public static final int KEY_F15;
    public static final int KEY_F16;
    public static final int KEY_F17;
    public static final int KEY_F18;
    public static final int KEY_F19;
    public static final int KEY_DELETE;
    public static final int KEY_HOME;
    public static final int KEY_END;

    private UKeyboard() {
    }

//    private final <T> T noInline(final Function0<? extends T> init) {
//        final int $i$f$noInline = 0;
//        return (T)init.invoke();
//    }

    
    public static final void allowRepeatEvents(final boolean enabled) {
        Keyboard.enableRepeatEvents(enabled);
    }

    
    public static final boolean isCtrlKeyDown() {
        boolean b;
        if (Minecraft.IS_RUNNING_ON_MAC) {
            final UKeyboard instance = UKeyboard.INSTANCE;
            if (!isKeyDown(UKeyboard.KEY_LMETA)) {
                final UKeyboard instance2 = UKeyboard.INSTANCE;
                if (!isKeyDown(UKeyboard.KEY_RMETA)) {
                    b = false;
                    return b;
                }
            }
            b = true;
        }
        else {
            final UKeyboard instance3 = UKeyboard.INSTANCE;
            if (!isKeyDown(UKeyboard.KEY_LCONTROL)) {
                final UKeyboard instance4 = UKeyboard.INSTANCE;
                if (!isKeyDown(UKeyboard.KEY_RCONTROL)) {
                    b = false;
                    return b;
                }
            }
            b = true;
        }
        return b;
    }

    
    public static final boolean isShiftKeyDown() {
        final UKeyboard instance = UKeyboard.INSTANCE;
        if (!isKeyDown(UKeyboard.KEY_LSHIFT)) {
            final UKeyboard instance2 = UKeyboard.INSTANCE;
            if (!isKeyDown(UKeyboard.KEY_RSHIFT)) {
                return false;
            }
        }
        return true;
    }

    
    public static final boolean isAltKeyDown() {
        final UKeyboard instance = UKeyboard.INSTANCE;
        if (!isKeyDown(UKeyboard.KEY_LMENU)) {
            final UKeyboard instance2 = UKeyboard.INSTANCE;
            if (!isKeyDown(UKeyboard.KEY_RMENU)) {
                return false;
            }
        }
        return true;
    }


    public static final UKeyboard.Modifiers getModifiers() {
        final UKeyboard instance = UKeyboard.INSTANCE;
        final boolean ctrlKeyDown = isCtrlKeyDown();
        final UKeyboard instance2 = UKeyboard.INSTANCE;
        final boolean shiftKeyDown = isShiftKeyDown();
        final UKeyboard instance3 = UKeyboard.INSTANCE;
        return new UKeyboard.Modifiers(ctrlKeyDown, shiftKeyDown, isAltKeyDown());
    }

    
    public static final boolean isKeyComboCtrlA(final int key) {
        if (key == UKeyboard.KEY_A) {
            final UKeyboard instance = UKeyboard.INSTANCE;
            if (isCtrlKeyDown()) {
                final UKeyboard instance2 = UKeyboard.INSTANCE;
                if (!isShiftKeyDown()) {
                    final UKeyboard instance3 = UKeyboard.INSTANCE;
                    if (!isAltKeyDown()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    
    public static final boolean isKeyComboCtrlC(final int key) {
        if (key == UKeyboard.KEY_C) {
            final UKeyboard instance = UKeyboard.INSTANCE;
            if (isCtrlKeyDown()) {
                final UKeyboard instance2 = UKeyboard.INSTANCE;
                if (!isShiftKeyDown()) {
                    final UKeyboard instance3 = UKeyboard.INSTANCE;
                    if (!isAltKeyDown()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    
    public static final boolean isKeyComboCtrlV(final int key) {
        if (key == UKeyboard.KEY_V) {
            final UKeyboard instance = UKeyboard.INSTANCE;
            if (isCtrlKeyDown()) {
                final UKeyboard instance2 = UKeyboard.INSTANCE;
                if (!isShiftKeyDown()) {
                    final UKeyboard instance3 = UKeyboard.INSTANCE;
                    if (!isAltKeyDown()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    
    public static final boolean isKeyComboCtrlX(final int key) {
        if (key == UKeyboard.KEY_X) {
            final UKeyboard instance = UKeyboard.INSTANCE;
            if (isCtrlKeyDown()) {
                final UKeyboard instance2 = UKeyboard.INSTANCE;
                if (!isShiftKeyDown()) {
                    final UKeyboard instance3 = UKeyboard.INSTANCE;
                    if (!isAltKeyDown()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    
    public static final boolean isKeyComboCtrlY(final int key) {
        if (key == UKeyboard.KEY_Y) {
            final UKeyboard instance = UKeyboard.INSTANCE;
            if (isCtrlKeyDown()) {
                final UKeyboard instance2 = UKeyboard.INSTANCE;
                if (!isShiftKeyDown()) {
                    final UKeyboard instance3 = UKeyboard.INSTANCE;
                    if (!isAltKeyDown()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    
    public static final boolean isKeyComboCtrlZ(final int key) {
        if (key == UKeyboard.KEY_Z) {
            final UKeyboard instance = UKeyboard.INSTANCE;
            if (isCtrlKeyDown()) {
                final UKeyboard instance2 = UKeyboard.INSTANCE;
                if (!isShiftKeyDown()) {
                    final UKeyboard instance3 = UKeyboard.INSTANCE;
                    if (!isAltKeyDown()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    
    public static final boolean isKeyComboCtrlShiftZ(final int key) {
        if (key == UKeyboard.KEY_Z) {
            final UKeyboard instance = UKeyboard.INSTANCE;
            if (isCtrlKeyDown()) {
                final UKeyboard instance2 = UKeyboard.INSTANCE;
                if (isShiftKeyDown()) {
                    final UKeyboard instance3 = UKeyboard.INSTANCE;
                    if (!isAltKeyDown()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    
    public static final boolean isKeyDown(final int key) {
        return key != UKeyboard.KEY_NONE && ((key < 0) ? Mouse.isButtonDown(key + 100) : (key < 256 && Keyboard.isKeyDown(key)));
    }

    public static final String getKeyName(final KeyBinding keyBinding) {
//        Intrinsics.checkNotNullParameter((Object)keyBinding, "keyBinding");
        return keyBinding.getKeyDescription(); // Hack: 我不确定
    }

//    @Deprecated(message = "Does not work for mouse bindings", replaceWith = @ReplaceWith(expression = "getKeyName(keyBinding)", imports = {}))
    public static final String getKeyName(final int keyCode, final int scanCode) {
        return Keyboard.getKeyName(keyCode);
    }

//    @Deprecated(message = "Does not work for mouse or scanCode-type bindings", replaceWith = @ReplaceWith(expression = "getKeyName(keyCode, -1)", imports = {}))
    public static final String getKeyName(final int keyCode) {
        final UKeyboard instance = UKeyboard.INSTANCE;
        return getKeyName(keyCode, -1);
    }

    static {
        INSTANCE = new UKeyboard();
        UKeyboard this_$iv = UKeyboard.INSTANCE;
        int $i$f$noInline = 0;
        final int n = 0;
        KEY_NONE = 0;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n2 = 0;
        KEY_ESCAPE = 1;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n3 = 0;
        KEY_LMETA = 219;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n4 = 0;
        KEY_RMETA = 220;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n5 = 0;
        KEY_LCONTROL = 29;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n6 = 0;
        KEY_RCONTROL = 157;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n7 = 0;
        KEY_LSHIFT = 42;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n8 = 0;
        KEY_RSHIFT = 54;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n9 = 0;
        KEY_LMENU = 56;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n10 = 0;
        KEY_RMENU = 184;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n11 = 0;
        KEY_MINUS = 12;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n12 = 0;
        KEY_EQUALS = 13;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n13 = 0;
        KEY_BACKSPACE = 14;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n14 = 0;
        KEY_ENTER = 28;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n15 = 0;
        KEY_TAB = 15;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n16 = 0;
        KEY_LBRACKET = 26;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n17 = 0;
        KEY_RBRACKET = 27;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n18 = 0;
        KEY_SEMICOLON = 39;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n19 = 0;
        KEY_APOSTROPHE = 40;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n20 = 0;
        KEY_GRAVE = 41;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n21 = 0;
        KEY_BACKSLASH = 43;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n22 = 0;
        KEY_COMMA = 51;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n23 = 0;
        KEY_PERIOD = 52;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n24 = 0;
        KEY_SLASH = 53;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n25 = 0;
        KEY_MULTIPLY = 55;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n26 = 0;
        KEY_SPACE = 57;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n27 = 0;
        KEY_CAPITAL = 58;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n28 = 0;
        KEY_LEFT = 203;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n29 = 0;
        KEY_UP = 200;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n30 = 0;
        KEY_RIGHT = 205;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n31 = 0;
        KEY_DOWN = 208;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n32 = 0;
        KEY_NUMLOCK = 69;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n33 = 0;
        KEY_SCROLL = 70;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n34 = 0;
        KEY_SUBTRACT = 74;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n35 = 0;
        KEY_ADD = 78;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n36 = 0;
        KEY_DIVIDE = 181;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n37 = 0;
        KEY_DECIMAL = 83;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n38 = 0;
        KEY_NUMPAD0 = 82;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n39 = 0;
        KEY_NUMPAD1 = 79;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n40 = 0;
        KEY_NUMPAD2 = 80;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n41 = 0;
        KEY_NUMPAD3 = 81;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n42 = 0;
        KEY_NUMPAD4 = 75;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n43 = 0;
        KEY_NUMPAD5 = 76;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n44 = 0;
        KEY_NUMPAD6 = 77;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n45 = 0;
        KEY_NUMPAD7 = 71;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n46 = 0;
        KEY_NUMPAD8 = 72;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n47 = 0;
        KEY_NUMPAD9 = 73;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n48 = 0;
        KEY_A = 30;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n49 = 0;
        KEY_B = 48;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n50 = 0;
        KEY_C = 46;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n51 = 0;
        KEY_D = 32;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n52 = 0;
        KEY_E = 18;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n53 = 0;
        KEY_F = 33;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n54 = 0;
        KEY_G = 34;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n55 = 0;
        KEY_H = 35;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n56 = 0;
        KEY_I = 23;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n57 = 0;
        KEY_J = 36;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n58 = 0;
        KEY_K = 37;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n59 = 0;
        KEY_L = 38;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n60 = 0;
        KEY_M = 50;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n61 = 0;
        KEY_N = 49;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n62 = 0;
        KEY_O = 24;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n63 = 0;
        KEY_P = 25;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n64 = 0;
        KEY_Q = 16;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n65 = 0;
        KEY_R = 19;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n66 = 0;
        KEY_S = 31;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n67 = 0;
        KEY_T = 20;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n68 = 0;
        KEY_U = 22;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n69 = 0;
        KEY_V = 47;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n70 = 0;
        KEY_W = 17;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n71 = 0;
        KEY_X = 45;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n72 = 0;
        KEY_Y = 21;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n73 = 0;
        KEY_Z = 44;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n74 = 0;
        KEY_0 = 11;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n75 = 0;
        KEY_1 = 2;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n76 = 0;
        KEY_2 = 3;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n77 = 0;
        KEY_3 = 4;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n78 = 0;
        KEY_4 = 5;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n79 = 0;
        KEY_5 = 6;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n80 = 0;
        KEY_6 = 7;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n81 = 0;
        KEY_7 = 8;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n82 = 0;
        KEY_8 = 9;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n83 = 0;
        KEY_9 = 10;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n84 = 0;
        KEY_F1 = 59;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n85 = 0;
        KEY_F2 = 60;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n86 = 0;
        KEY_F3 = 61;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n87 = 0;
        KEY_F4 = 62;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n88 = 0;
        KEY_F5 = 63;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n89 = 0;
        KEY_F6 = 64;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n90 = 0;
        KEY_F7 = 65;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n91 = 0;
        KEY_F8 = 66;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n92 = 0;
        KEY_F9 = 67;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n93 = 0;
        KEY_F10 = 68;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n94 = 0;
        KEY_F11 = 87;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n95 = 0;
        KEY_F12 = 88;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n96 = 0;
        KEY_F13 = 100;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n97 = 0;
        KEY_F14 = 101;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n98 = 0;
        KEY_F15 = 102;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n99 = 0;
        KEY_F16 = 103;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n100 = 0;
        KEY_F17 = 104;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n101 = 0;
        KEY_F18 = 105;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n102 = 0;
        KEY_F19 = 113;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n103 = 0;
        KEY_DELETE = 211;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n104 = 0;
        KEY_HOME = 199;
        this_$iv = UKeyboard.INSTANCE;
        $i$f$noInline = 0;
        final int n105 = 0;
        KEY_END = 207;
    }

    public static final class Modifiers
    {
        private final boolean isCtrl;
        private final boolean isShift;
        private final boolean isAlt;

        public Modifiers(final boolean isCtrl, final boolean isShift, final boolean isAlt) {
            this.isCtrl = isCtrl;
            this.isShift = isShift;
            this.isAlt = isAlt;
        }

        public final boolean isCtrl() {
            return this.isCtrl;
        }

        public final boolean isShift() {
            return this.isShift;
        }

        public final boolean isAlt() {
            return this.isAlt;
        }

        public final boolean component1() {
            return this.isCtrl;
        }

        public final boolean component2() {
            return this.isShift;
        }

        public final boolean component3() {
            return this.isAlt;
        }

        public final Modifiers copy(final boolean isCtrl, final boolean isShift, final boolean isAlt) {
            return new Modifiers(isCtrl, isShift, isAlt);
        }

        @Override
        public String toString() {
            return "Modifiers(isCtrl=" + this.isCtrl + ", isShift=" + this.isShift + ", isAlt=" + this.isAlt + ')';
        }

        @Override
        public int hashCode() {
            int isCtrl;
            if ((isCtrl = (this.isCtrl ? 1 : 0)) != 0) {
                isCtrl = 1;
            }
            int result = isCtrl;
            final int n = result * 31;
            int isShift;
            if ((isShift = (this.isShift ? 1 : 0)) != 0) {
                isShift = 1;
            }
            result = n + isShift;
            final int n2 = result * 31;
            int isAlt;
            if ((isAlt = (this.isAlt ? 1 : 0)) != 0) {
                isAlt = 1;
            }
            result = n2 + isAlt;
            return result;
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Modifiers)) {
                return false;
            }
            final Modifiers modifiers = (Modifiers)other;
            return this.isCtrl == modifiers.isCtrl && this.isShift == modifiers.isShift && this.isAlt == modifiers.isAlt;
        }
    }

}
