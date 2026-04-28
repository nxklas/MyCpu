package de.nxklas.mycpu.core;

import de.nxklas.mycpu.util.Tuple;

public enum AccessMode {
    IMMEDIATE(0b00),
    REGISTER(0b01);

    public final byte value;

    AccessMode(int value) {
        this.value = (byte) (value & 0b11);
    }

    public static byte encode(AccessMode dst, AccessMode src) {
        return (byte) ((dst.value << 2) | src.value);
    }

    public static Tuple<AccessMode, AccessMode> decode(byte mode) {
        var value1 = fromValue((byte) ((mode >> 2) & 0b11));
        var value2 = fromValue((byte) ((mode >> 0) & 0b11));
        return new Tuple<AccessMode, AccessMode>(value1, value2);
    }

    public static AccessMode fromValue(byte value) {
        for (var mode : values()) {
            if (mode.value == value) {
                return mode;
            }
        }

        throw new IllegalArgumentException("Argument 'value' was not a supported access mode: " + value);
    }
}
