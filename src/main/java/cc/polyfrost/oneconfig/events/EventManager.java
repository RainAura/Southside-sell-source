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

package cc.polyfrost.oneconfig.events;

import cc.polyfrost.oneconfig.config.core.exceptions.InvalidTypeException;
import cc.polyfrost.oneconfig.events.event.*;
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack;
import dev.diona.southside.Southside;
import dev.diona.southside.event.PacketType;
import dev.diona.southside.event.events.*;
import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import me.kbrewster.eventbus.EventBus;
import me.kbrewster.eventbus.exception.ExceptionHandler;
import me.kbrewster.eventbus.invokers.LMFInvoker;
import net.optifine.shaders.config.MacroExpressionResolver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.lwjglx.input.Keyboard;

import java.util.HashSet;
import java.util.Set;

/**
 * Manages all events from OneConfig.
 */
public final class EventManager {
    /**
     * The instance of the {@link EventManager}.
     */
    public static final EventManager INSTANCE = new EventManager();
    private static final Logger LOGGER = LogManager.getLogger("OneConfig/EventManager");
    private final EventBus eventBus = new EventBus(new LMFInvoker(), new OneConfigExceptionHandler());
    private final Set<Object> listeners = new HashSet<>();

    public EventManager() {
        Southside.eventBus.subscribe(this);
    }

//    @EventListener(priority = ListenerPriority.HIGHEST)
//    public void onRender2D(Render2DEvent event) {
//        this.post(new HudRenderEvent(new UMatrixStack(), event.getPartialTicks()));
//    }

    @EventListener(priority = ListenerPriority.HIGHEST)
    public void onKey(KeyEvent event) {
        int state = 0;
        if (Keyboard.getEventKeyState()) {
            if (Keyboard.isRepeatEvent()) {
                state = 2;
            } else {
                state = 1;
            }
        }
        this.post(new RawKeyEvent(Keyboard.getEventKey(), state));
    }

    @EventListener(priority = ListenerPriority.HIGHEST)
    public void onMouse(MouseEvent event) {
        this.post(new RawMouseEvent(event.getButton(), event.getState()));
    }

    @EventListener(priority = ListenerPriority.HIGHEST)
    public void onWorld(WorldEvent event) {
        this.post(new WorldLoadEvent());
    }

    @EventListener(priority = ListenerPriority.HIGHEST)
    public void onPacket(PacketEvent event) {
        if (event.getType() == PacketType.RECEIVE) {
            ReceivePacketEvent e = new ReceivePacketEvent(event.getPacket());
            this.post(e);
            if (e.isCancelled) event.setCancelled(true);
        } else {
            SendPacketEvent e = new SendPacketEvent(event.getPacket());
            this.post(e);
            if (e.isCancelled) event.setCancelled(true);
        }
    }


    /**
     * Returns the {@link EventBus} instance.
     *
     * @return The {@link EventBus} instance.
     */
    public EventBus getEventBus() {
        return eventBus;
    }

    /**
     * Registers an object to the {@link EventBus}.
     *
     * @param object The object to register.
     * @see EventBus#register(Object)
     */
    public void register(Object object) {
        if (listeners.add(object)) {
            eventBus.register(object);
        } else {
            LOGGER.warn("Attempted to register an already registered listener: " + object);
        }
    }

    /**
     * Unregisters an object from the {@link EventBus}.
     *
     * @param object The object to unregister.
     * @see EventBus#unregister(Object)
     */
    public void unregister(Object object) {
        listeners.remove(object);
        eventBus.unregister(object);
    }

    /**
     * Posts an event to the {@link EventBus}.
     *
     * @param event The event to post.
     * @see EventBus#post(Object)
     */
    public void post(Object event) {
        eventBus.post(event);
    }


    /**
     * Bypass to allow special exceptions to actually crash
     */
    private static class OneConfigExceptionHandler implements ExceptionHandler {
        @Override
        public void handle(@NotNull Exception e) {
            if (e instanceof InvalidTypeException) {
                throw (InvalidTypeException) e;
            }
            if (e instanceof IllegalArgumentException) {
                throw (IllegalArgumentException) e;
            } else e.printStackTrace();
        }
    }
}
