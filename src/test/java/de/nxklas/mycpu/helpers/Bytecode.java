package de.nxklas.mycpu.helpers;

import de.nxklas.mycpu.core.AccessMode;
import de.nxklas.mycpu.core.Opcode;

public final class Bytecode {
    public static final byte IMM = AccessMode.IMMEDIATE.value;
    public static final byte IMM_TO_IMM = AccessMode.encode(AccessMode.IMMEDIATE, AccessMode.IMMEDIATE);
    public static final byte IMM_TO_REG = AccessMode.encode(AccessMode.REGISTER, AccessMode.IMMEDIATE);
    public static final byte REG_TO_REG = AccessMode.encode(AccessMode.REGISTER, AccessMode.REGISTER);

    public static final byte MOV = Opcode.MOV.value;
    public static final byte ADD = Opcode.ADD.value;
    public static final byte SUB = Opcode.SUB.value;
    public static final byte CMP = Opcode.CMP.value;    
    public static final byte JMP = Opcode.JMP.value;
    public static final byte JMP_EQUALS = Opcode.JMP_EQUALS.value;
    public static final byte JMP_NOTEQUALS = Opcode.JMP_NOTEQUALS.value;
    public static final byte JMP_LESS = Opcode.JMP_LESS.value;
    public static final byte JMP_LESS_OR_EQUALS = Opcode.JMP_LESS_OR_EQUALS.value;
    public static final byte JMP_GREATER = Opcode.JMP_GREATER.value;
    public static final byte JMP_GREATER_OR_EQUALS = Opcode.JMP_GREATER_OR_EQUALS.value;
    public static final byte HALT = Opcode.HALT.value;

    private Bytecode() {
    }
}
