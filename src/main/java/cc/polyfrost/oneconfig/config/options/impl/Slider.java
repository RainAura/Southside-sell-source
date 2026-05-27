
package cc.polyfrost.oneconfig.config.options.impl;


import cc.polyfrost.oneconfig.config.data.OptionType;
import cc.polyfrost.oneconfig.config.options.Option;
import org.jetbrains.annotations.NotNull;

public class Slider extends Option<java.lang.Number> {

    private final float min;

    private final float max;

    private final int step;

    private final String description;

    private final boolean instant;
    private final float increment;

    public Slider(@NotNull String label, float min, float max, int step, String description, boolean instant, float increment, @NotNull java.lang.Number defaultValue) {
        super(label, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
        this.description = description;
        this.instant = instant;
        this.increment = increment;
    }
    public Slider(@NotNull String label, float min, float max, int step, String description, boolean instant, @NotNull java.lang.Number defaultValue) {
        this(label, min, max, step, description, instant, 0.1F, defaultValue);
    }

    public Slider(String name, java.lang.Number value, java.lang.Number minimum, java.lang.Number maximum) {
        this(name,minimum.floatValue(),maximum.floatValue(),0,"",false,0.1F,value);
    }

    public Slider(String name, java.lang.Number value, java.lang.Number minimum, java.lang.Number maximum,int step) {
        this(name,minimum.floatValue(),maximum.floatValue(),step,"",false,0.1F,value);
    }

    public Slider(String name, java.lang.Number value, java.lang.Number minimum, java.lang.Number maximum,java.lang.Number increment) {
        this(name,minimum.floatValue(),maximum.floatValue(),0,"",false, increment.floatValue(),value);
    }


    public Slider(String name, int value, int minimum, int maximum,int increment) {
        this(name, value, minimum, maximum, (double) increment);
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public int getStep() {
        return step;
    }

    public String getDescription() {
        return description;
    }

    public boolean isInstant() {
        return instant;
    }

    public float getIncrement() {
        return increment;
    }

    @Override
    public OptionType type() {
        return OptionType.SLIDER;
    }
}
