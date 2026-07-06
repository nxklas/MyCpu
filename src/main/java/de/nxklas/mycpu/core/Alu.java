package de.nxklas.mycpu.core;

public class Alu {

    public static final byte FLAG_NONE = 0;
    public static final byte FLAG_NEG = 1;
    public static final byte FLAG_ZERO = 1 << 1;
    public static final byte FLAG_CARRY = 1 << 2;

    private byte flags;

    public Alu() {
        flags = FLAG_NONE;
    }

    public byte flags() {
        return flags;
    }

    public int add(int a, int b) {
        a = a & 0xFF;
        b = b & 0xFF;

        var rawResult = a + b;
        var result = rawResult & 0xFF;

        flags = FLAG_NONE;

        updateZeroAndNeg(result);

        if (rawResult > 0xFF)
            flags |= FLAG_CARRY;

        return result;
    }

    public int sub(int a, int b) {
        a = a & 0xFF;
        b = b & 0xFF;

        var result = (a - b) & 0xFF;

        flags = FLAG_NONE;

        updateZeroAndNeg(result);

        if (a < b)
            flags |= FLAG_CARRY;

        return result;
    }

    private void updateZeroAndNeg(int result) {
        if (result == 0)
            flags |= FLAG_ZERO;

        if ((result & 0x80) != 0)
            flags |= FLAG_NEG;
    }
}
