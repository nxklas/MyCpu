package de.nxklas.mycpu.core.instructions;

import de.nxklas.mycpu.core.operands.Operand;

public final record JmpInstruction(JmpKind kind, Operand dst) implements Instruction {
}
