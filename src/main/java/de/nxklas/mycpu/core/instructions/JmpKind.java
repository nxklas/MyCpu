package de.nxklas.mycpu.core.instructions;

import de.nxklas.mycpu.core.Alu;

public enum JmpKind {
    None(0),
    Equals(Alu.FLAG_ZERO),
    NotEquals(Alu.FLAG_NEG | Alu.FLAG_CARRY),
    Less(Alu.FLAG_NEG),
    LessOrEquals(Alu.FLAG_NEG | Alu.FLAG_ZERO),
    Greater(Alu.FLAG_CARRY),
    GreaterOrEquals(Alu.FLAG_CARRY | Alu.FLAG_ZERO);

    public final byte value;

    JmpKind(int value) {
        this.value = (byte)(value & 0xFF);
    }
}
