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

package cc.polyfrost.oneconfig.config;

import cc.polyfrost.oneconfig.config.core.ConfigUtils;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.config.core.OneKeyBind;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.PageLocation;
import cc.polyfrost.oneconfig.config.elements.BasicOption;
import cc.polyfrost.oneconfig.config.elements.OptionPage;
import cc.polyfrost.oneconfig.config.elements.OptionSubcategory;
import cc.polyfrost.oneconfig.config.options.Option;
import cc.polyfrost.oneconfig.config.options.WrappedValue;
import cc.polyfrost.oneconfig.config.options.impl.HUD;
import cc.polyfrost.oneconfig.config.options.impl.Page;
import cc.polyfrost.oneconfig.gui.OneConfigGui;
import cc.polyfrost.oneconfig.gui.elements.config.ConfigHeader;
import cc.polyfrost.oneconfig.gui.elements.config.ConfigKeyBind;
import cc.polyfrost.oneconfig.gui.elements.config.ConfigPageButton;
import cc.polyfrost.oneconfig.gui.pages.ModConfigPage;
import cc.polyfrost.oneconfig.hud.BasicHud;
import cc.polyfrost.oneconfig.hud.Position;
import cc.polyfrost.oneconfig.internal.config.core.ConfigCore;
import cc.polyfrost.oneconfig.internal.config.core.KeyBindHandler;
import cc.polyfrost.oneconfig.internal.hud.HudCore;
import cc.polyfrost.oneconfig.utils.gui.GuiUtils;
import com.google.gson.*;
import dev.diona.southside.Southside;
import dev.diona.southside.module.Module;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "ResultOfMethodCallIgnored"})
public class Config {

    public final transient HashMap<String, BasicOption> optionNames = new HashMap<>();
    protected final transient String configFile;
    protected final transient Gson gson = addGsonOptions(new GsonBuilder()).create();
    protected final transient List<Consumer<Boolean>> toggleListener = new ArrayList<>();
    public final transient Mod mod;

    public WrappedValue<Boolean> enabled = new WrappedValue<>("Enabled", false) {
        @Override
        public void setValue(Boolean value) {
            super.setValue(value);
            toggleListener.forEach((e)->e.accept(value));
        }
    };
    public final boolean canToggle;

    private final transient Logger logger;

    /**
     * @param modData    information about the mod
     * @param configFile file where config is stored
     * @param enabled    whether the mod is enabled or not
     */
    public Config(Mod modData, String configFile, boolean enabled, boolean canToggle) {
        this.configFile = configFile;
        this.mod = modData;
        this.enabled.setValue(enabled);
        this.canToggle = canToggle;



        this.logger = LogManager.getLogger(getClass());
    }

    public Config(Mod modData, String configFile, boolean enabled) {
        this(modData, configFile, enabled, true);
    }

    /**
     * @param modData    information about the mod
     * @param configFile file where config is stored
     */
    public Config(Mod modData, String configFile) {
        this(modData, configFile, false);
    }

    public void initialize() {
        logger.trace("Initializing config for {}...", mod.name);

        boolean migrate = false;
        File profileFile = ConfigUtils.getProfileFile(configFile);
        if (profileFile.exists()) load();
        if (!profileFile.exists()) {
            if (mod.migrator != null) migrate = true;
            else save();
        }

        logger.trace("Should migrate: {}", migrate);

        mod.config = this;
        generateOptionList(this, mod.defaultPage, mod, migrate);
        if (migrate) save();

        logger.trace("Config for {} initialized", mod.name);
        ConfigCore.mods.add(mod);
    }

    public void setEnable(boolean enabled) {
        this.enabled.setValue(enabled);
    }

    public boolean isEnabled() {
        return this.enabled.getValue();
    }

    public void registerToggleListener(Consumer<Boolean> l) {
        toggleListener.add(l);
    }

    public void reInitialize() {
        logger.trace("Reinitializing config for {}...", mod.name);

        File profileFile = ConfigUtils.getProfileFile(configFile);
        if (profileFile.exists()) load();
        if (!profileFile.exists()) {
            save();
        }
    }

    public void reset() {
        try {
            this.parseConfig(null, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Save current config to file
     */
    public void save() {
        logger.trace("Saving config for {}...", mod.name);

        Path profilePath = ConfigUtils.getProfileFile(configFile).toPath();
//        Path nonProfileSpecificPath = ConfigUtils.getNonProfileSpecificFile(configFile).toPath();

//        logger.trace("Saving to:\n\t{}\n\t{}", profilePath, nonProfileSpecificPath);
        logger.trace("Saving to:\n\t{}", profilePath);

        try {
            Files.createDirectories(profilePath.getParent());
//            Files.createDirectories(nonProfileSpecificPath.getParent());
        } catch (IOException e) {
            logger.error("Failed to create directories for config file", e);
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(profilePath), StandardCharsets.UTF_8))) {
            JsonObject config = new JsonObject();
            saveConfig(config, this);
            writer.write(gson.toJson(config));
        } catch (Exception e) {
            e.printStackTrace();
        }
//        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(nonProfileSpecificPath), StandardCharsets.UTF_8))) {
//            writer.write(saveConfig());
//            writer.write(nonProfileSpecificGson.toJson(this));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * Load file and overwrite current values
     */
    public void load() {
        logger.trace("Loading config for {}...", mod.name);

        Path profilePath = ConfigUtils.getProfileFile(configFile).toPath();
//        Path nonProfileSpecificPath = ConfigUtils.getNonProfileSpecificFile(configFile).toPath();

//        logger.trace("Loading from:\n\t{}\n\t{}", profilePath, nonProfileSpecificPath);
        logger.trace("Loading from:\n\t{}", profilePath);


        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(profilePath), StandardCharsets.UTF_8))) {
            JsonObject config = new JsonParser().parse(reader).getAsJsonObject();
            parseConfig(config, this);
//            gson.fromJson(reader, this.getClass());
        } catch (Exception e) {
            e.printStackTrace();
            File file = ConfigUtils.getProfileFile(configFile);
            file.renameTo(new File(file.getParentFile(), file.getName() + ".corrupted"));
        }
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(nonProfileSpecificPath), StandardCharsets.UTF_8))) {
//            JsonObject config = new JsonParser().parse(reader).getAsJsonObject();
//            this.parseConfig(config);

//            nonProfileSpecificGson.fromJson(reader, this.getClass());
//        } catch (Exception e) {
//            e.printStackTrace();
//            File file = ConfigUtils.getNonProfileSpecificFile(configFile);
//            file.renameTo(new File(file.getParentFile(), file.getName() + ".corrupted"));
//        }
    }

    @SuppressWarnings("unchecked")
    public static void parseConfig(JsonObject config, Object object) throws Exception {
        for (Field field : object.getClass().getFields()) {
            Object obj;

            boolean darkMagic = false;
            if (object instanceof WrappedValue<?> || object instanceof Option<?>) {
                obj = object;
                darkMagic = true;
            } else {
                boolean staticFlag = (Modifier.isStatic(field.getModifiers()));
                if (!field.canAccess(staticFlag ? null : object) || Modifier.isTransient(field.getModifiers()))
                    continue;
                obj = field.get(staticFlag ? null : object);
            }

            if (obj instanceof WrappedValue<?> wrappedValue) {
                String label = wrappedValue.getLabel();
                Class<?> targetClass = wrappedValue.getTargetClass();
                if (config == null) {
                    wrappedValue.reset();
                    if (label.equals("Enabled") && object instanceof Module module) {
                        if (((Boolean) wrappedValue.getDefaultValue())) {
                            Southside.eventBus.subscribe(module);
                        } else {
                            Southside.eventBus.unsubscribe(module);
                        }
                    }
                    continue;
                }
                JsonElement value = config.get(label);
                if (value != null) {
                    if (targetClass.isAssignableFrom(Boolean.class)) {
                        WrappedValue<Boolean> casted = (WrappedValue<Boolean>) wrappedValue;
                        casted.setValue(value.getAsBoolean());
                        if (label.equals("Enabled") && object instanceof Module module) {
                            if (value.getAsBoolean()) {
                                Southside.eventBus.subscribe(module);
                            } else {
                                Southside.eventBus.unsubscribe(module);
                            }
                        }
                    } else if (targetClass.isAssignableFrom(Character.class)) {
                        WrappedValue<Character> casted = (WrappedValue<Character>) wrappedValue;
                        casted.setValue(value.getAsCharacter());
                    } else if (targetClass.isAssignableFrom(Short.class)) {
                        WrappedValue<Short> casted = (WrappedValue<Short>) wrappedValue;
                        casted.setValue(value.getAsShort());
                    } else if (targetClass.isAssignableFrom(Byte.class)) {
                        WrappedValue<Byte> casted = (WrappedValue<Byte>) wrappedValue;
                        casted.setValue(value.getAsByte());
                    } else if (targetClass.isAssignableFrom(Integer.class)) {
                        WrappedValue<Integer> casted = (WrappedValue<Integer>) wrappedValue;
                        casted.setValue(value.getAsInt());
                    } else if (targetClass.isAssignableFrom(Float.class)) {
                        WrappedValue<Float> casted = (WrappedValue<Float>) wrappedValue;
                        casted.setValue(value.getAsFloat());
                    } else if (targetClass.isAssignableFrom(Long.class)) {
                        WrappedValue<Long> casted = (WrappedValue<Long>) wrappedValue;
                        casted.setValue(value.getAsLong());
                    } else if (targetClass.isAssignableFrom(Double.class)) {
                        WrappedValue<Double> casted = (WrappedValue<Double>) wrappedValue;
                        casted.setValue(value.getAsDouble());
                    } else if (targetClass.isAssignableFrom(String.class)) {
                        WrappedValue<String> casted = (WrappedValue<String>) wrappedValue;
                        casted.setValue(value.getAsString());
                    } else if (targetClass.isAssignableFrom(Position.class)) {
                        WrappedValue<Position> casted = (WrappedValue<Position>) wrappedValue;
                        JsonObject positionObject = value.getAsJsonObject();
                        Position position = casted.getValue();
                        position = new Position(
                                position.getHud(),
                                position.x,
                                position.y,
                                position.getWidth(),
                                position.getHeight()
                        );
                        int anchor = Integer.parseInt(positionObject.get("anchor").getAsString());
                        float x = positionObject.get("x").getAsFloat();
                        float y = positionObject.get("y").getAsFloat();
                        position.anchor = Position.AnchorPosition.values()[anchor];
                        position.x = x;
                        position.y = y;
                    }
                }
            } else if (obj instanceof Option<?> option) {
                String label = option.getLabel();
                if (config == null) {
                    if (obj instanceof HUD) {
                        parseConfig(null, option.getValue());
                    } else if (obj instanceof Page) {
                        Object pageInstance = option.getValue();
                        if (!(pageInstance instanceof cc.polyfrost.oneconfig.gui.pages.Page)) {
                            parseConfig(null, option.getValue());
                        }
                    } else {
                        option.reset();
                    }
                    continue;
                }
                JsonElement value = config.get(label);
                if (value != null) {
                    if (obj instanceof HUD) {
                        parseConfig(value.getAsJsonObject(), option.getValue());
                    } else if (obj instanceof Page) {
                        Object pageInstance = option.getValue();
                        if (!(pageInstance instanceof cc.polyfrost.oneconfig.gui.pages.Page)) {
                            parseConfig(value.getAsJsonObject(), option.getValue());
                        }
                    } else {
                        switch (option.type()) {
                            case SWITCH, CHECKBOX, DUAL_OPTION -> {
                                Option<Boolean> casted = (Option<Boolean>) option;
                                casted.setValue(value.getAsBoolean());
                            }
                            case TEXT -> {
                                Option<String> casted = (Option<String>) option;
                                casted.setValue(value.getAsString());
                            }
                            case SLIDER, NUMBER -> {
                                Option<Number> casted = (Option<Number>) option;
                                casted.setValue(value.getAsNumber());
                            }
                            case COLOR -> {
                                Option<OneColor> casted = (Option<OneColor>) option;
                                JsonObject jsonObject = value.getAsJsonObject();
                                JsonArray hsba = jsonObject.get("hsba").getAsJsonArray();
                                JsonPrimitive dataBit = jsonObject.get("dataBit").getAsJsonPrimitive();

                                OneColor oneColor = new OneColor(hsba.get(0).getAsNumber().intValue(),
                                        hsba.get(1).getAsNumber().intValue(),
                                        hsba.get(2).getAsNumber().intValue(),
                                        hsba.get(3).getAsNumber().intValue(),
                                        dataBit.getAsInt());
                                casted.setValue(oneColor);
                            }
                            case DROPDOWN -> {
                                Option<Number> casted = (Option<Number>) option;
                                casted.setValue(value.getAsNumber().intValue());
                            }
                            case KEYBIND -> {
                                Option<OneKeyBind> casted = (Option<OneKeyBind>) option;
                                JsonObject jsonObject = value.getAsJsonObject();
                                JsonArray keyBinds = jsonObject.get("keyBinds").getAsJsonArray();
                                int[] keyBindsArray = new int[keyBinds.size()];

                                for (int i = 0; i < keyBinds.size(); i++) {
                                    keyBindsArray[i] = keyBinds.get(i).getAsNumber().intValue();
                                }

//                                OneKeyBind oneKeyBind = new OneKeyBind(keyBindsArray);
//                                casted.setValue(oneKeyBind);
                                casted.value = new OneKeyBind();
                                for (int key : keyBindsArray) {
                                    casted.value.keyBinds.add(key);
                                }
                                casted.value.setRunnable(casted.getDefaultValue().getRunnable());
                                if (casted.basicOption != null) casted.basicOption.triggerListeners();
                            }
                        }
                    }
                }

            }

            if (darkMagic) {
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static JsonObject saveConfig(JsonObject config, Object object) throws Exception {
        for (Field field : object.getClass().getFields()) {
            Object obj;

            boolean darkMagic = false;
            if (object instanceof WrappedValue<?> || object instanceof Option<?>) {
                obj = object;
                darkMagic = true;
            } else {
                boolean staticFlag = (Modifier.isStatic(field.getModifiers()));
                if (!field.canAccess(staticFlag ? null : object) || Modifier.isTransient(field.getModifiers()))
                    continue;
                obj = field.get(staticFlag ? null : object);
            }

            if (obj instanceof WrappedValue<?> wrappedValue) {
                String label = wrappedValue.getLabel();
                Class<?> targetClass = wrappedValue.getTargetClass();
                Object value = wrappedValue.getValue();
                if (value != null) {
                    if (targetClass.isAssignableFrom(Boolean.class)) {
                        config.addProperty(label, (Boolean) value);
                    } else if (targetClass.isAssignableFrom(Character.class)) {
                        config.addProperty(label, (Character) value);
                    } else if (targetClass.isAssignableFrom(Short.class)) {
                        config.addProperty(label, (Short) value);
                    } else if (targetClass.isAssignableFrom(Byte.class)) {
                        config.addProperty(label, (Byte) value);
                    } else if (targetClass.isAssignableFrom(Integer.class)) {
                        config.addProperty(label, (Integer) value);
                    } else if (targetClass.isAssignableFrom(Float.class)) {
                        config.addProperty(label, (Float) value);
                    } else if (targetClass.isAssignableFrom(Long.class)) {
                        config.addProperty(label, (Long) value);
                    } else if (targetClass.isAssignableFrom(Double.class)) {
                        config.addProperty(label, (Double) value);
                    } else if (targetClass.isAssignableFrom(String.class)) {
                        config.addProperty(label, (String) value);
                    } else if (targetClass.isAssignableFrom(Position.class)) {
                        Position position = (Position) value;
                        JsonObject positionObject = new JsonObject();
                        positionObject.addProperty("anchor", String.valueOf(position.anchor.ordinal()));
                        positionObject.addProperty("x", position.x);
                        positionObject.addProperty("y", position.y);

                        config.add(label, positionObject);
                    }
                }
            } else if (obj instanceof Option<?> option) {
                String label = option.getLabel();
                Object value = option.getValue();
                if (value != null) {
                    if (obj instanceof HUD) {
                        config.add(label, saveConfig(new JsonObject(), value));
                    } else if (obj instanceof Page) {
                        Object pageInstance = option.getValue();
                        if (!(pageInstance instanceof cc.polyfrost.oneconfig.gui.pages.Page)) {
                            config.add(label, saveConfig(new JsonObject(), value));
                        }
                    } else {
                        switch (option.type()) {
                            case SWITCH, CHECKBOX, DUAL_OPTION -> {
                                config.addProperty(label, (Boolean) value);
                            }
                            case TEXT -> {
                                config.addProperty(label, (String) value);
                            }
                            case SLIDER, NUMBER -> {
                                config.addProperty(label, (Number) value);
                            }
                            case COLOR -> {
                                OneColor oneColor = (OneColor) value;
                                JsonObject oneColorObject = new JsonObject();
                                JsonArray hsba = new JsonArray();
                                for (short i : oneColor.hsba) {
                                    hsba.add(i);
                                }
                                oneColorObject.add("hsba", hsba);
                                oneColorObject.addProperty("dataBit", oneColor.dataBit);

                                config.add(label, oneColorObject);
                            }
                            case DROPDOWN -> {
                                config.addProperty(label, (Integer) value);
                            }
                            case KEYBIND -> {
                                OneKeyBind oneKeyBind = (OneKeyBind) value;
                                JsonObject oneKeyBindObject = new JsonObject();
                                JsonArray keyBinds = new JsonArray();
                                for (Integer keyBind : oneKeyBind.keyBinds) {
                                    keyBinds.add(keyBind);
                                }
                                oneKeyBindObject.add("keyBinds", keyBinds);

                                config.add(label, oneKeyBindObject);
                            }
                        }
                    }
                }
            }

            if (darkMagic) {
                break;
            }
        }
        return config;
    }

    /**
     * Generate the option list, for internal use only
     *
     * @param instance instance of target class
     * @param page     page to add options too
     * @param mod      data about the mod
     * @param migrate  whether the migrator should be run
     */
    protected final ArrayList<BasicOption> generateOptionList(Object instance, OptionPage page, Mod mod, boolean migrate) {
        return generateOptionList(new ArrayList<>(), instance, instance.getClass(), page, mod, migrate);
    }

    /**
     * Generate the option list, for internal use only
     *
     * @param instance    instance of target class
     * @param targetClass which class to lookup into
     * @param page        page to add options too
     * @param mod         data about the mod
     * @param migrate     whether the migrator should be run
     */
    protected final ArrayList<BasicOption> generateOptionList(ArrayList<BasicOption> des, Object instance, Class<?> targetClass, OptionPage page, Mod mod, boolean migrate, Object... context) {
        Objects.requireNonNull(des);
        logger.trace("Generating option list for {}... (targetting={})", mod.name, targetClass.getName());

        Class<?> superclass = targetClass.getSuperclass();
        if (superclass != Object.class) {
            des.addAll(generateOptionList(new ArrayList<>(), instance, superclass, page, mod, migrate, context));
        }

//        String pagePath = page.equals(mod.defaultPage) ? "" : page.name + ".";
//        for (Field field : targetClass.getDeclaredFields()) {
//            cc.polyfrost.oneconfig.internal.config.annotations.Option option = ConfigUtils.findAnnotation(field, cc.polyfrost.oneconfig.internal.config.annotations.Option.class);
//            CustomOption customOption = ConfigUtils.findAnnotation(field, CustomOption.class);
//            String optionName = pagePath + field.getName();
//            // TODO: 这里改掉
//            // 改成通过 cc.polyfrost.oneconfig.config.options 获取出 BasicOption 的方式
//            // 这样是最不需要大改的
//            if (option != null) {
//                BasicOption configOption = ConfigUtils.addOptionToPage(page, option, field, instance, migrate ? mod.migrator : null);
//                optionNames.put(optionName, configOption);
//            } else if (customOption != null) {
//                BasicOption configOption = getCustomOption(field, customOption, page, mod, migrate);
//                if (configOption == null) continue;
//                optionNames.put(optionName, configOption);
//            } else if (field.isAnnotationPresent(cc.polyfrost.oneconfig.config.annotations.Page.class)) {
//                cc.polyfrost.oneconfig.config.annotations.Page optionPage = field.getAnnotation(cc.polyfrost.oneconfig.config.annotations.Page.class);
//                OptionSubcategory subcategory = ConfigUtils.getSubCategory(page, optionPage.category(), optionPage.subcategory());
//                Object pageInstance = ConfigUtils.getField(field, instance);
//                if (pageInstance == null) continue;
//                ConfigPageButton button;
//                if (pageInstance instanceof cc.polyfrost.oneconfig.gui.pages.Page) {
//                    button = new ConfigPageButton(field, instance, optionPage.name(), optionPage.description(), optionPage.category(), optionPage.subcategory(), (cc.polyfrost.oneconfig.gui.pages.Page) pageInstance);
//                } else {
//                    OptionPage newPage = new OptionPage(optionPage.name(), mod);
//                    generateOptionList(pageInstance, newPage, mod, migrate);
//                    button = new ConfigPageButton(field, instance, optionPage.name(), optionPage.description(), optionPage.category(), optionPage.subcategory(), newPage);
//                }
//                if (optionPage.location() == PageLocation.TOP) subcategory.topButtons.add(button);
//                else subcategory.bottomButtons.add(button);
//            } else if (field.isAnnotationPresent(cc.polyfrost.oneconfig.config.annotations.HUD.class)) {
//                HUDUtils.addHudOptions(page, field, instance, this);
//            }
//        }
        for (Field field : targetClass.getDeclaredFields()) {
            boolean debugFlag = "lineProperty".equals(field.getName());
            if (debugFlag)
                System.out.println("I FOUND!");
//            if (Modifier.isStatic(field.getModifiers())) continue;
            boolean staticFlag = Modifier.isStatic(field.getModifiers());
            if (debugFlag)
                System.out.println("1");
            if (!field.canAccess(staticFlag ? null : instance)) continue;
            if (debugFlag)
                System.out.println("2");
            Object obj = null;
            try {
                obj = field.get(staticFlag ? null : instance);
            } catch (IllegalAccessException e) {
                continue;
            }
            if (debugFlag) {
                System.out.println("3");
                System.out.println(obj instanceof Option<?>);
                System.out.println(obj.getClass().getName());
            }

            if (obj instanceof Option<?> option) {
                if (obj instanceof HUD hudOption) {
//                    HUDUtils.addHudOptions(page, hudOption, this);

                    hudOption.getValue().position.getValue().setHud(hudOption.getValue());
                    hudOption.getValue().setConfig(this);
                    HudCore.huds.put(hudOption, hudOption.getValue());
                    ArrayList<BasicOption> basicOptions = generateOptionList(new ArrayList<>() {
                        {
                            add(new ConfigHeader(
                                    null,
                                    null,
                                    hudOption.getLabel(),
                                    hudOption.category(),
                                    hudOption.subcategory(),
                                    2
                            ));
                        }
                    }, hudOption.getValue(), hudOption.getValue().getClass(), page, mod, migrate, hudOption.category(), hudOption.subcategory());
                    HudCore.hudOptions.addAll(basicOptions);
                    if (hudOption.getValue() instanceof BasicHud hud) {
                        try {
                            boolean changed = false;
                            if (hud.paddingX.getValue().floatValue() > 10f) {
                                hud.paddingX.setValue(5f);
                            }
                            if (hud.paddingY.getValue().floatValue() > 10f) {
                                hud.paddingY.setValue(5f);
                            }
                            if (changed) this.save();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    // TODO: add dependency
                    ConfigUtils.getSubCategory(page, option.category(), option.subcategory()).options.addAll(basicOptions);
                } else if (obj instanceof Page pageOption) {
                    OptionSubcategory subcategory = ConfigUtils.getSubCategory(page, pageOption.category(), pageOption.subcategory());
                    Object pageInstance = pageOption.getValue();
                    if (pageInstance == null) continue;
                    ConfigPageButton button;
                    if (pageInstance instanceof cc.polyfrost.oneconfig.gui.pages.Page) {
                        button = new ConfigPageButton(field, instance, pageOption.getLabel(), pageOption.getDescription(), pageOption.category(), pageOption.subcategory(), (cc.polyfrost.oneconfig.gui.pages.Page) pageInstance);
                    } else {
                        OptionPage newPage = new OptionPage(pageOption.getLabel(), mod);
                        generateOptionList(pageInstance, newPage, mod, migrate);
                        button = new ConfigPageButton(field, instance, pageOption.getLabel(), pageOption.getDescription(), pageOption.category(), pageOption.subcategory(), newPage);
                    }
                    if (pageOption.getLocation() == PageLocation.TOP) subcategory.topButtons.add(button);
                    else subcategory.bottomButtons.add(button);
                } else {
                    if (debugFlag)
                        System.out.println("4");
                    BasicOption configOption;
                    if (context.length == 2) {
                        if (debugFlag)
                            System.out.println("5");
                        configOption = ConfigUtils.getOption2(option, (String) context[0], (String) context[1]);
                        optionNames.put(option.getLabel(), configOption);
                    } else {
                        configOption = ConfigUtils.addOptionToPage2(page, option);
                        if (debugFlag)
                            System.out.println("6");
                        optionNames.put(option.getLabel(), configOption);
                    }
                    des.add(configOption);
                }
            }
        }
//        for (Method method : targetClass.getDeclaredMethods()) {
//            Button button = ConfigUtils.findAnnotation(method, Button.class);
//            String optionName = pagePath + method.getName();
//            if (button != null) {
//                BasicOption option = ConfigUtils.addOptionToPage(page, method, instance);
//                optionNames.put(optionName, option);
//            }
//        }
        logger.trace("Finished generating option list for {} (targetting={})", mod.name, targetClass.getName());
        return des;
    }


    protected GsonBuilder addGsonOptions(GsonBuilder builder) {
        return builder
                .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                .setPrettyPrinting();
    }

    /**
     * Function to open the gui of this mod
     */
    public void openGui() {
        if (mod == null) return;
        GuiUtils.displayScreen(new OneConfigGui(new ModConfigPage(mod.defaultPage)));
    }

    /**
     * Disable an option if a certain condition is not met
     *
     * @param option        The name of the field, or if the field is in a page "pageName.fieldName"
     * @param conditionName The name of the condition, this is used in the GUI
     * @param condition     The condition that has to be met for the option to be enabled
     */
    protected final void addDependency(String option, String conditionName, Supplier<Boolean> condition) {
        if (!optionNames.containsKey(option)) return;
        optionNames.get(option).addDependency(conditionName, condition);
    }

    /**
     * Disable an option if a certain condition is not met
     *
     * @param option    The name of the field, or if the field is in a page "pageName.fieldName"
     * @param condition The condition that has to be met for the option to be enabled
     */
//    @Deprecated
    protected final void addDependency(String option, Supplier<Boolean> condition) {
//        Deprecator.markDeprecated();
        if (!optionNames.containsKey(option)) return;
        optionNames.get(option).addDependency(condition);
    }

    /**
     * Disable an option if a certain condition is not met
     *
     * @param option          The name of the field, or if the field is in a page "pageName.fieldName"
     * @param dependentOption The option that has to be enabled
     */
    protected final void addDependency(String option, String dependentOption) {
        if (!optionNames.containsKey(option) || !optionNames.containsKey(dependentOption)) return;
        BasicOption optionObj = optionNames.get(dependentOption);
        optionNames.get(option).addDependency(optionObj.name, () -> {
            try {
                return (boolean) optionObj.get();
            } catch (IllegalAccessException ignored) {
                return true;
            }
        });
    }

    /**
     * Disable an option if a certain condition is not met
     *
     * @param option The name of the field, or if the field is in a page "pageName.fieldName"
     * @param value  The value of the dependency
     */
    @Deprecated
    protected final void addDependency(String option, boolean value) {
//        Deprecator.markDeprecated();
        if (!optionNames.containsKey(option)) return;
        optionNames.get(option).addDependency(() -> value);
    }

    /**
     * Hide an option if a certain condition is met
     *
     * @param option    The name of the field, or if the field is in a page "pageName.fieldName"
     * @param condition The condition that has to be met for the option to be hidden
     */
    protected final void hideIf(String option, Supplier<Boolean> condition) {
        if (!optionNames.containsKey(option)) return;
        optionNames.get(option).addHideCondition(condition);
    }

    /**
     * Disable an option if a certain condition is not met
     *
     * @param option          The name of the field, or if the field is in a page "pageName.fieldName"
     * @param dependentOption The option that has to be hidden
     */
    protected final void hideIf(String option, String dependentOption) {
        if (!optionNames.containsKey(option) || !optionNames.containsKey(dependentOption)) return;
        optionNames.get(option).addHideCondition(() -> {
            try {
                return (boolean) optionNames.get(dependentOption).get();
            } catch (IllegalAccessException ignored) {
                return true;
            }
        });
    }

    /**
     * Hide an option if a certain condition is met
     *
     * @param option The name of the field, or if the field is in a page "pageName.fieldName"
     * @param value  The value of the condition
     */
    @Deprecated
    protected final void hideIf(String option, boolean value) {
//        Deprecator.markDeprecated();
        if (!optionNames.containsKey(option)) return;
        optionNames.get(option).addHideCondition(() -> value);
    }

    /**
     * Register a new listener for when an option changes
     *
     * @param option   The name of the field, or if the field is in a page "pageName.fieldName"
     * @param runnable What should be executed after the option is changed
     */
    protected final void addListener(String option, Runnable runnable) {
        if (!optionNames.containsKey(option)) return;
        optionNames.get(option).addListener(runnable);
    }

    /**
     * Register an action to a keybind
     *
     * @param keyBind  The keybind
     * @param runnable The code to be executed
     */
    protected final void registerKeyBind(OneKeyBind keyBind, Runnable runnable) {
        Field field = null;
        Object instance = null;
        for (BasicOption option : optionNames.values()) {
            if (!(option instanceof ConfigKeyBind)) continue;
            try {
                Field f = option.getField();
                OneKeyBind keyBind1 = (OneKeyBind) option.get();
                if (keyBind1 != keyBind) continue;
                field = f;
                instance = option.getParent();
            } catch (IllegalAccessException ignored) {
                continue;
            }
            break;
        }
        keyBind.setRunnable(runnable);
        KeyBindHandler.INSTANCE.addKeyBind(field, instance, keyBind);
    }

    /**
     * @return If this mod supports profiles, false for compatibility mode
     */
    public boolean supportsProfiles() {
        return true;
    }

    /**
     * Register a mod to be managed by OneConfig. <br>
     * <b>NOTE: DO NOT USE THIS METHOD UNLESS YOU ARE USING A CUSTOM IMPLEMENTATION!</b> This function is normally completed by initializing a Config. <br>
     *
     * @param mod The mod to be registered
     * @implNote null -> null, if already registered -> old mod, if registered successfully -> null
     */
    @ApiStatus.Experimental
    @Contract("null -> null")
    public static Mod register(Mod mod) {
        if (mod == null) return null;
        if (ConfigCore.mods.contains(mod)) return mod;
        ConfigCore.mods.add(mod);
        ConfigCore.sortMods();
        return null;
    }

    /**
     * Literally does nothing.
     * <p>
     * As configs HAVE to be initialized before your mod loader's post-init, instances need to be created before that.
     * Hence, this method exists so config instances which are located in the actual class instead of the main mod class can be created.
     * </p>
     * For example:
     * <pre>{@code
     * public class MyConfig {
     *     // The INSTANCE class is located here, and initialize is called in the constructor.
     *     // This means that if we do not call preload, the config will not be initialized in time.
     *     public static final MyConfig INSTANCE = new MyConfig();
     *
     *     public MyConfig() {
     *         super(whatever);
     *         initialize();
     *     }
     * }
     *
     * public class MyMod {
     *     public void initialize() {
     *         MyConfig.INSTANCE.preload(); // This makes sure the config is initialized before the mod loader's post-init.
     *     }
     * }
     * }</pre>
     */
    public final void preload() {

    }
}
