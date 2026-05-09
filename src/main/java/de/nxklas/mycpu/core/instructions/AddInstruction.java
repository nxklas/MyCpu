package de.nxklas.mycpu.core.instructions;

import de.nxklas.mycpu.core.operands.Operand;

public final class AddInstruction implements Instruction{
    public final Operand dst;
    public final Operand src;

    public AddInstruction(Operand dst, Operand src) {
        this.dst = dst;
        this.src = src;
    }
}
