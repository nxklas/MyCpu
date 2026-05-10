package de.nxklas.mycpu.core;

public class Alu {
    public static final byte FLAG_NONE = 0;
    public static final byte FLAG_NEG = 1;
    public static final byte FLAG_ZERO = FLAG_NEG << 1;
    public static final byte FLAG_CARRY = FLAG_ZERO << 1;
    private byte flags;

    public Alu() {
        this.flags = FLAG_NONE;
    }

    public int add(int a, int b) {
        var result = a + b;
        setFlags(result);
        return result;
    }

    public int sub(int a, int b) {
        var result = a - b;
        setFlags(result);
        return result;
    }

    public byte flags() {
        return flags;
    }

    private void setFlags(long result) {
        flags = FLAG_NONE;

        if (result < 0)
            flags |= FLAG_NEG;
        else if (result == 0)
            flags |= FLAG_ZERO;
        else if (result >= Integer.MAX_VALUE)
            flags |= FLAG_CARRY;
    }
}
