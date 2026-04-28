package de.nxklas.mycpu.core.operands;

public final class ImmediateOperand implements Operand {
    public final int value;

    public ImmediateOperand(int value) {
        this.value = value;
    }
}
