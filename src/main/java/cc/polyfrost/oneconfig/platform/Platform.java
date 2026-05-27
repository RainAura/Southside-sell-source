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

package cc.polyfrost.oneconfig.platform;

import cc.polyfrost.oneconfig.platform.impl.*;

import java.util.ServiceLoader;

/**
 * Contains various platform-specific utilities for OneConfig.
 * <p>
 * This is meant for internal usage, however other mods may use these (unless otherwise stated).
 */
public interface Platform {

    static Platform getInstance() {
        return PlatformHolder.INSTANCE.platform;
    }

    static MousePlatform getMousePlatform() {
        return PlatformHolder.INSTANCE.mousePlatform;
    }

//    static LoaderPlatform getLoaderPlatform() {
//        return PlatformHolder.INSTANCE.loaderPlatform;
//    }

    static ServerPlatform getServerPlatform() {
        return PlatformHolder.INSTANCE.serverPlatform;
    }

    static GLPlatform getGLPlatform() {
        return PlatformHolder.INSTANCE.glPlatform;
    }

    static GuiPlatform getGuiPlatform() {
        return PlatformHolder.INSTANCE.guiPlatform;
    }

    static I18nPlatform getI18nPlatform() {
        return PlatformHolder.INSTANCE.i18nPlatform;
    }

    boolean isCallingFromMinecraftThread();

    int getMinecraftVersion();

    boolean isDevelopmentEnvironment();

    Loader getLoader();

    class PlatformHolder {
        private PlatformHolder() {

        }

        static PlatformHolder INSTANCE = new PlatformHolder();
        Platform platform = new PlatformImpl(); // ServiceLoader
        MousePlatform mousePlatform = new MousePlatformImpl();// ServiceLoader
//        LoaderPlatform loaderPlatform = ServiceLoader.load(LoaderPlatform.class, LoaderPlatform.class.getClassLoader()).iterator().next();
        ServerPlatform serverPlatform = new ServerPlatformImpl();// ServiceLoader
        GLPlatform glPlatform = new GLPlatformImpl();
        GuiPlatform guiPlatform = new GuiPlatformImpl();
        I18nPlatform i18nPlatform = new I18nPlatformImpl();
    }

    enum Loader {
        FORGE,
        FABRIC
    }
}
