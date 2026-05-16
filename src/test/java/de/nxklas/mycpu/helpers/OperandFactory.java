package de.nxklas.mycpu.helpers;

import de.nxklas.mycpu.core.operands.ImmediateOperand;
import de.nxklas.mycpu.core.operands.RegisterOperand;

public class OperandFactory {
    public static ImmediateOperand immediate(int value) {
        return new ImmediateOperand(value);
    }

    public static RegisterOperand register(int index) {
        return new RegisterOperand(index);
    }
}
