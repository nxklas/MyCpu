package de.nxklas.mycpu.core.operands;

public final class RegisterOperand implements Operand {
    public final int index;

    public RegisterOperand(int index) {
        this.index = index;
    }
}
