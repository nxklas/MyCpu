package de.nxklas.mycpu.core.instructions;

import de.nxklas.mycpu.core.operands.Operand;

public final record SubInstruction(Operand dst, Operand src) implements Instruction {
}
