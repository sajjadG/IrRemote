package com.nullpx.irremote;

/**
 * Created by sajjadg on 1/27/17.
 */
public class IrSignal {

    private final int[] durationPattern;
    private final int frequency;

    public IrSignal(int frequency, int[] durationPattern) {
        this.frequency = frequency;
        this.durationPattern = durationPattern;
    }

    public int[] getDurationPattern() {
        return durationPattern;
    }

    public int getFrequency() {
        return frequency;
    }
}
