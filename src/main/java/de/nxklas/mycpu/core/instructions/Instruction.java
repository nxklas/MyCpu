package de.nxklas.mycpu.core.instructions;

public sealed interface Instruction permits NopInstruction, MovInstruction, HaltInstruction {
}
