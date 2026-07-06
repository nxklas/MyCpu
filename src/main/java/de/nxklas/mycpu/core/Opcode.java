package de.nxklas.mycpu.core;

public enum Opcode {
    NOP(0x00),
    MOV(0x10),
    ADD(0x20),
    SUB(0x30),
    CMP(0x40),
    JMP(0x50),
    JMP_EQUALS(0x51),
    JMP_NOTEQUALS(0x52),
    JMP_LESS(0x53),
    JMP_LESS_OR_EQUALS(0x54),
    JMP_GREATER(0x55),
    JMP_GREATER_OR_EQUALS(0x56),
    HALT(0xFF);

    public final byte value;

    Opcode(int value) {
        this.value = (byte) (value & 0xFF);
    }

    public static Opcode fromValue(byte value) {
        for (var opcode : values()) {
            if (opcode.value == value)
                return opcode;
        }

        throw new IllegalArgumentException("Argument 'value' was not a supported opcode: " + value);
    }
}
