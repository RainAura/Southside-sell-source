/*
 * This file is part of OneConfig.
 * OneConfig - Next Generation Config Library for Minecraft: Java Edition
 * Copyright (C) 2021~2023 Polyfrost.
 *   <https://polyfrost.cc> <https://github.com/Polyfrost/>
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *   OneConfig is licensed under the terms of version 3 of the GNU Lesser
 * General Public License as published by the Free Software Foundation, AND
 * under the Additional Terms Applicable to OneConfig, as published by Polyfrost,
 * either version 1.0 of the Additional Terms, or (at your option) any later
 * version.
 *
 *   This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 * License.  If not, see <https://www.gnu.org/licenses/>. You should
 * have also received a copy of the Additional Terms Applicable
 * to OneConfig, as published by Polyfrost. If not, see
 * <https://polyfrost.cc/legal/oneconfig/additional-terms>
 */

package cc.polyfrost.oneconfig.internal.config;

import cc.polyfrost.oneconfig.config.core.OneKeyBind;
import cc.polyfrost.oneconfig.config.options.WrappedValue;
import cc.polyfrost.oneconfig.config.options.impl.*;
import cc.polyfrost.oneconfig.gui.OneConfigGui;
import cc.polyfrost.oneconfig.internal.gui.BlurHandler;
import cc.polyfrost.oneconfig.libs.universal.UKeyboard;
import cc.polyfrost.oneconfig.platform.Platform;
import cc.polyfrost.oneconfig.utils.TickDelay;
import dev.diona.southside.util.player.ChatUtil;

public class Preferences extends InternalConfig {
    public static Preferences INSTANCE;
    public KeyBind oneConfigKeyBind = new KeyBind("Click GUI Keybind", "Choose what key opens the OneConfig UI", 2, new OneKeyBind(UKeyboard.KEY_RSHIFT)) {
        @Override
        public String subcategory() {
            return "GUI Settings";
        }
    };

    public Switch enableBlur = new Switch("Enable Blur", true) {
        @Override
        public String subcategory() {
            return "GUI Settings";
        }
    };

    public Switch enableCustomScale = new Switch("Use custom GUI scale", false) {
        @Override
        public String subcategory() {
            return "GUI Settings";
        }
    };

    public Switch enableCustomNotificationScale = new Switch("Use custom Notification scale", false) {
        @Override
        public String subcategory() {
            return "GUI Settings";
        }
    };

//    @Slider(
//            name = "Custom GUI scale",
//            subcategory = "GUI Settings",
//            min = 0.5f,
//            max = 2f
//    )
//    public static float customScale = 1f;
    public Slider customScale = new Slider(
            "Custom GUI scale",
            0.5f,
            2f,
            0,
            "",
            false,
            1f
    ) {
        @Override
        public String subcategory() {
            return "GUI Settings";
        }
    };

    public Slider customNotificationScale = new Slider(
            "Custom Notification scale",
            0.5f,
            2f,
            0,
            "",
            false,
            1f
    ) {
        @Override
        public String subcategory() {
            return "GUI Settings";
        }
    };

    public Dropdown openingBehavior = new Dropdown("Opening Behavior", new String[]{
            "Mods",
            "Preferences",
            "Previous page",
            "Smart reset"
    }, "Choose which page will show when you open OneConfig", 2, 3) {
        @Override
        public String category() {
            return "Behavior";
        }

        @Override
        public String subcategory() {
            return "GUI Settings";
        }
    };

    public Switch showPageAnimationOnOpen = new Switch("Show opening page animation", "Whether or not to show the page switch animation when opening OneConfig", 2, false) {
        @Override
        public String category() {
            return "Behavior";
        }

        @Override
        public String subcategory() {
            return "GUI Settings";
        }
    };

    public Slider timeUntilReset = new Slider("Time before reset", 5, 60, 0, "How much time (in seconds) before opening back to the \"Mods\" page", false, 15) {
        @Override
        public String category() {
            return "Behavior";
        }

        @Override
        public String subcategory() {
            return "GUI Settings";
        }
    };

    public cc.polyfrost.oneconfig.config.options.impl.Number searchDistance = new cc.polyfrost.oneconfig.config.options.impl.Number(
            "Search Distance",
            0, 10, 0,
            2,
            "The maximum Levenshtein distance to search for similar config names",
            2
    ) {
        @Override
        public String category() {
            return "Behavior";
        }

        @Override
        public String subcategory() {
            return "Search";
        }
    };

    public Switch guiOpenAnimation = new Switch(
            "Opening Animation",
            "Plays an animation when opening the GUI",
            true
    ) {
        @Override
        public String category() {
            return "Animations";
        }

        @Override
        public String subcategory() {
            return "Global";
        }
    };

    public Switch guiClosingAnimation = new Switch(
            "Closing Animation",
            "Plays an animation when closing the GUI",
            true
    ) {
        @Override
        public String category() {
            return "Animations";
        }

        @Override
        public String subcategory() {
            return "Global";
        }
    };

    public Slider animationTime = new Slider(
            "Opening Time",
            0.05f,
            2f,
            0,
            "The duration of the opening and closing animations, in seconds",
            false,
            0.6f
    ) {
        @Override
        public String category() {
            return "Animations";
        }

        @Override
        public String subcategory() {
            return "Global";
        }
    };
    public Dropdown animationType = new Dropdown(
            "Opening Type",
            new String[]{"Subtle", "Full"},
            "The type of opening/closing animation to use",
            1,
            0
    ) {
        @Override
        public String category() {
            return "Animations";
        }

        @Override
        public String subcategory() {
            return "Global";
        }
    };

    public Switch showPageAnimations = new Switch(
            "Show Page Animations",
            "Enables or disables the page switch animation",
            true
    ) {
        @Override
        public String category() {
            return "Animations";
        }

        @Override
        public String subcategory() {
            return "Pages";
        }
    };

    public Slider pageAnimationDuration = new Slider(
            "Page Animation Duration",
            0.1f,
            0.6f,
            0,
            "The duration of the page switch animation, in seconds",
            false,
            0.3f
    ) {
        @Override
        public String category() {
            return "Animations";
        }

        @Override
        public String subcategory() {
            return "Pages";
        }
    };

    public Switch toggleSwitchBounce = new Switch(
            "Toggle Switch Bounce",
            "Enables or disables the bounce animation on toggle switches",
            true
    ) {
        @Override
        public String category() {
            return "Animations";
        }

        @Override
        public String subcategory() {
            return "Toggles";
        }
    };

    public Slider trackerResponseDuration = new Slider(
            "Tracker Response Time",
            0f,
            100f,
            0,
            "The time it takes for the slider tracker to move, in milliseconds",
            false,
            60
    ) {
        @Override
        public String category() {
            return "Animations";
        }

        @Override
        public String subcategory() {
            return "Sliders";
        }
    };


    public Button button = new Button(
            "Show First Launch Message",
            "Show",
            "Shows the first launch message again",
            1,
            () -> {
                firstLaunch.setValue(true);
                oneconfigOpened = false;
                save();
            });


    public static WrappedValue<Boolean> firstLaunch = new WrappedValue<>("First Launch", true);
    public static transient boolean oneconfigOpened = false;


    public Preferences() {
        super("Preferences", "Preferences.json");
        INSTANCE = this;
        initialize();
        addListener(enableBlur.getLabel(), () -> BlurHandler.INSTANCE.reloadBlur(Platform.getGuiPlatform().getCurrentScreen()));
        registerKeyBind(oneConfigKeyBind.getValue(), () -> {
            new TickDelay(() -> Platform.getGuiPlatform().setCurrentScreen(OneConfigGui.create()), 1);
        });
        addListener(animationTime.getLabel(), () -> {
            if (Preferences.INSTANCE.guiOpenAnimation.getValue()) {
                // Force reset the animation
                OneConfigGui.INSTANCE.isClosed = true;
            }
        });
        addDependency(guiClosingAnimation.getLabel(), guiOpenAnimation.getLabel());
        addDependency(timeUntilReset.getLabel(), "Smart Opening Behavior", () -> openingBehavior.getValue() == 3);
        addDependency(pageAnimationDuration.getLabel(), showPageAnimations.getLabel());
        INSTANCE = this;
    }

    public static Preferences getInstance() {
        return INSTANCE == null ? (INSTANCE = new Preferences()) : INSTANCE;
    }
}
