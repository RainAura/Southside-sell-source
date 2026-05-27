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

package cc.polyfrost.oneconfig.config.core;

import cc.polyfrost.oneconfig.config.core.exceptions.InvalidTypeException;
import cc.polyfrost.oneconfig.config.data.OptionType;
import cc.polyfrost.oneconfig.config.elements.BasicOption;
import cc.polyfrost.oneconfig.config.elements.OptionCategory;
import cc.polyfrost.oneconfig.config.elements.OptionPage;
import cc.polyfrost.oneconfig.config.elements.OptionSubcategory;
import cc.polyfrost.oneconfig.config.migration.Migrator;
import cc.polyfrost.oneconfig.config.options.impl.*;
import cc.polyfrost.oneconfig.config.options.impl.Number;
import cc.polyfrost.oneconfig.gui.elements.config.*;
import cc.polyfrost.oneconfig.internal.config.annotations.Option;
import cc.polyfrost.oneconfig.internal.config.profiles.Profiles;
import com.google.gson.FieldAttributes;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;

public class ConfigUtils {
    public static BasicOption getOption2(cc.polyfrost.oneconfig.config.options.Option option) {
        return getOption2(option, option.category(), option.subcategory());
    }
    public static BasicOption getOption2(cc.polyfrost.oneconfig.config.options.Option option, String category, String subcategory) {
        Field field = null;
        try {
            field = cc.polyfrost.oneconfig.config.options.Option.class.getDeclaredField("value");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        field.setAccessible(true);
        var instance = option;

        switch (option.type()) {
            case SWITCH: {
                Switch casted = (Switch) option;
                return new ConfigSwitch(field, instance, casted.getLabel(), casted.getDescription(), category, subcategory, casted.getSize());
            }
            case CHECKBOX: {
                Checkbox casted = (Checkbox) option;
                return new ConfigCheckbox(field, instance, casted.getLabel(), casted.getDescription(), category, subcategory, casted.getSize());
            }
            case INFO: {
                Info casted = (Info) option;
                return new ConfigInfo(field, instance, casted.getDefaultValue(), category, subcategory, casted.getSize(), casted.getType());
            }
            case HEADER: {
                Header casted = (Header) option;
                return new ConfigHeader(field, instance, casted.getValue(), category, subcategory, casted.getSize());
            }
            case COLOR: {
                Color casted = (Color) option;
                return new ConfigColorElement(field, instance, casted.getLabel(), casted.getDescription(), category, subcategory, casted.getSize(), casted.isAllowAlpha());
            }
            case DROPDOWN: {
                Dropdown casted = (Dropdown) option;
                return new ConfigDropdown(field, instance, casted.getLabel(), casted.getDescription(), category, subcategory, casted.getSize(), casted.getOptions());
            }
            case TEXT: {
                Text casted = (Text) option;
                return new ConfigTextBox(field, instance, casted.getLabel(), casted.getDescription(), category, subcategory, casted.isSecure() || casted.isMultiline() ? 2 : casted.getSize(), casted.getPlaceholder(), casted.isSecure(), casted.isMultiline());
            }
            case BUTTON: {
                Button casted = (Button) option;
                return new ConfigButton(casted.getValue(), instance, casted.getLabel(), casted.getDescription(), category, subcategory, casted.getSize(), casted.getText());
            }
            case SLIDER: {
                Slider casted = (Slider) option;
                return new ConfigSlider(field, instance, casted.getLabel(), casted.getDescription(), category, subcategory, casted.getMin(), casted.getMax(), casted.getStep(), casted.isInstant(), casted.getIncrement());
            }
            case NUMBER: {
                Number casted = (Number) option;
                return new ConfigNumber(field, instance, casted.getLabel(), casted.getDescription(), category, subcategory, casted.getMin(), casted.getMax(), casted.getStep(), casted.getSize());
            }
            case KEYBIND: {
                KeyBind casted = (KeyBind) option;
                return new ConfigKeyBind(field, instance, casted.getLabel(), casted.getDescription(), category, subcategory, casted.getSize());
            }
            case DUAL_OPTION: {
                DualOption casted = (DualOption) option;
                return new ConfigDualOption(field, instance, casted.getDescription(), casted.getDescription(), category, subcategory, casted.getSize(), casted.getLeft(), casted.getRight());
            }
        }
        return null;
    }

    public static void check(String type, Field field, Class<?>... expectedType) {
        // I have tried to check for supertype classes like Boolean other ways.
        // because they actually don't extend their primitive types (because that is impossible) so isAssignableFrom doesn't work.
        for (Class<?> clazz : expectedType) {
            if (clazz.isAssignableFrom(field.getType())) return;
        }
        throw new InvalidTypeException("Field " + field.getName() + " in config " + field.getDeclaringClass().getName() + " is annotated as a " + type + ", but is not of valid type, expected " + Arrays.toString(expectedType) + " (found " + field.getType() + ")");
    }

    public static ArrayList<BasicOption> getClassOptions(Object object) {
//        ArrayList<BasicOption> options = new ArrayList<>();
//        ArrayList<Field> fields = getClassFields(object.getClass());
//        for (Field field : fields) {
//            Option option = findAnnotation(field, Option.class);
//            if (option == null) continue;
//            options.add(getOption(option, field, object));
//        }
//        return options;

        ArrayList<BasicOption> options = new ArrayList<>();
        ArrayList<Field> fields = getClassFields(object.getClass());
        for (Field field : fields) {
//            Option option = findAnnotation(field, Option.class);
//            if (Modifier.isStatic(field.getModifiers())) continue;
            boolean staticFlag = Modifier.isStatic(field.getModifiers());
            if (!field.canAccess(staticFlag ? null : object)) continue;
            try {
                if (!(field.get(staticFlag ? null : object) instanceof cc.polyfrost.oneconfig.config.options.Option<?> option)) continue;
                options.add(getOption2(option));
            } catch (IllegalAccessException e) { }
        }
        return options;
    }

    public static ArrayList<Field> getClassFields(Class<?> object) {
        ArrayList<Field> fields = new ArrayList<>(Arrays.asList(object.getDeclaredFields()));
        Class<?> parentClass = object;
        Class<?> clazz = object;
        while (true) {
            clazz = clazz.getSuperclass();
            if (clazz != null && clazz != parentClass) fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            else break;
            parentClass = clazz;
        }
        return fields;
    }

//    public static BasicOption addOptionToPage(OptionPage page, Option option, Field field, Object instance, @Nullable Migrator migrator) {
//        BasicOption configOption = getOption(option, field, instance);
//        if (configOption == null) return null;
//        if (migrator != null) {
//            Object value = migrator.getValue(field, configOption.name, configOption.category, configOption.subcategory);
//            if (value != null) setField(field, value, instance);
//        }
//        getSubCategory(page, configOption.category, configOption.subcategory).options.add(configOption);
//        return configOption;
//    }

    public static BasicOption addOptionToPage2(OptionPage page, cc.polyfrost.oneconfig.config.options.Option option) {
        BasicOption configOption = getOption2(option);
        if (configOption == null) return null;
        getSubCategory(page, configOption.category, configOption.subcategory).options.add(configOption);
        return configOption;
    }

//    public static BasicOption addOptionToPage(OptionPage page, Method method, Object instance) {
//        BasicOption configOption = ConfigButton.create(method, instance);
//        getSubCategory(page, configOption.category, configOption.subcategory).options.add(configOption);
//        return configOption;
//    }

    public static OptionSubcategory getSubCategory(OptionPage page, String categoryName, String subcategoryName) {
        if (!page.categories.containsKey(categoryName)) page.categories.put(categoryName, new OptionCategory());
        OptionCategory category = page.categories.get(categoryName);
        OptionSubcategory subcategory = category.subcategories.stream().filter(s -> s.getName().equals(subcategoryName)).findFirst().orElse(null);
        if (category.subcategories.size() == 0 || subcategory == null) {
            category.subcategories.add((subcategory = new OptionSubcategory(subcategoryName, categoryName)));
        }
        return subcategory;
    }

    public static <T extends Annotation> T findAnnotation(Field field, Class<T> annotationType) {
        if (field.isAnnotationPresent(annotationType)) return field.getAnnotation(annotationType);
        for (Annotation ann : field.getDeclaredAnnotations()) {
            if (ann.annotationType().isAnnotationPresent(annotationType))
                return ann.annotationType().getAnnotation(annotationType);
        }
        return null;
    }

    public static <T extends Annotation> T findAnnotation(Method method, Class<T> annotationType) {
        if (method.isAnnotationPresent(annotationType)) return method.getAnnotation(annotationType);
        for (Annotation ann : method.getDeclaredAnnotations()) {
            if (ann.annotationType().isAnnotationPresent(annotationType))
                return ann.annotationType().getAnnotation(annotationType);
        }
        return null;
    }

    public static <T extends Annotation> T findAnnotation(FieldAttributes field, Class<T> annotationType) {
        T annotation = field.getAnnotation(annotationType);
        if (annotation != null) return annotation;
        for (Annotation ann : field.getAnnotations()) {
            if (ann.annotationType().isAnnotationPresent(annotationType))
                return ann.annotationType().getAnnotation(annotationType);
        }
        return null;
    }

    public static <T extends Annotation> T findAnnotation(Class<?> clazz, Class<T> annotationType) {
        if (clazz.isAnnotationPresent(annotationType)) return clazz.getAnnotation(annotationType);
        for (Annotation ann : clazz.getDeclaredAnnotations()) {
            if (ann.annotationType().isAnnotationPresent(annotationType))
                return ann.annotationType().getAnnotation(annotationType);
        }
        return null;
    }

    public static Object getField(Field field, Object parent) {
        try {
            field.setAccessible(true);
            return field.get(parent);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static void setField(Field field, Object value, Object parent) {
        try {
            field.setAccessible(true);
            field.set(parent, value);
        } catch (Exception ignored) {
        }
    }

    public static String getCurrentProfile() {
        return Profiles.getCurrentProfile();
    }

    public static File getProfileDir() {
        return Profiles.getProfileDir();
    }

//    public static File getNonSpecificProfileDir() {
//        return Profiles.nonProfileSpecificDir;
//    }

    public static File getProfileFile(String file) {
        return Profiles.getProfileFile(file);
    }

//    public static File getNonProfileSpecificFile(String file) {
//        return Profiles.getNonProfileSpecificFile(file);
//    }
}
