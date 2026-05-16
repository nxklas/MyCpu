package de.nxklas.mycpu.core.instructions;

import de.nxklas.mycpu.core.operands.Operand;

public final record MovInstruction(Operand dst, Operand src) implements Instruction {
}
