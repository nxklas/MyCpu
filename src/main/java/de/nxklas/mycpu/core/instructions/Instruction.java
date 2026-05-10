package de.nxklas.mycpu.core.instructions;

public sealed interface Instruction permits NopInstruction, MovInstruction, AddInstruction, SubInstruction, CmpInstruction, HaltInstruction {
}
