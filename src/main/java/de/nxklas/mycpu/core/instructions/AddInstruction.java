package de.nxklas.mycpu.core.instructions;

import de.nxklas.mycpu.core.operands.Operand;

public final record AddInstruction(Operand dst, Operand src) implements Instruction {
}
