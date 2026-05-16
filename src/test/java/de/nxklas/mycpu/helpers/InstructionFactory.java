package de.nxklas.mycpu.helpers;

import de.nxklas.mycpu.core.instructions.AddInstruction;
import de.nxklas.mycpu.core.instructions.CmpInstruction;
import de.nxklas.mycpu.core.instructions.MovInstruction;
import de.nxklas.mycpu.core.instructions.SubInstruction;
import de.nxklas.mycpu.core.operands.Operand;

public class InstructionFactory {
    public static AddInstruction add(Operand dst, Operand src) {
        return new AddInstruction(dst, src);
    }

    public static CmpInstruction cmp(Operand dst, Operand src) {
        return new CmpInstruction(dst, src);
    }

    public static MovInstruction mov(Operand dst, Operand src) {
        return new MovInstruction(dst, src);
    }

    public static SubInstruction sub(Operand dst, Operand src) {
        return new SubInstruction(dst, src);
    }
}
