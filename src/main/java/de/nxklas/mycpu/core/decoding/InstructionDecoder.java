package de.nxklas.mycpu.core.decoding;

import de.nxklas.mycpu.core.instructions.*;
import de.nxklas.mycpu.core.operands.*;

import java.util.ArrayList;

import de.nxklas.mycpu.core.AccessMode;
import de.nxklas.mycpu.core.Opcode;

import de.nxklas.mycpu.util.Tuple;

public class InstructionDecoder {
    private final byte[] bytecode;
    private int position;

    public InstructionDecoder(byte[] bytecode) {
        this.bytecode = bytecode;
        this.position = 0;
    }

    public Instruction[] decodeAllInstructions() {
        var result = new ArrayList<Instruction>();

        while(position < bytecode.length) {
            var instruction = fetch();
            result.add(instruction);
        }
        
        return result.toArray(new Instruction[0]);
    }

    Instruction fetch() {
        var opcode = next();
        return decode(opcode);
    }

    private Instruction decode(byte opcode) {
        Tuple<Operand, Operand> dstSrcPair;

        switch (Opcode.fromValue(opcode)) {
            case NOP:
                return new NopInstruction();
            case MOV:
                dstSrcPair = readDstSrcPair();
                return new MovInstruction(dstSrcPair.value1, dstSrcPair.value2);
            case ADD:
                dstSrcPair = readDstSrcPair();
                return new AddInstruction(dstSrcPair.value1, dstSrcPair.value2);
            case SUB:
                dstSrcPair = readDstSrcPair();
                return new SubInstruction(dstSrcPair.value1, dstSrcPair.value2);
            case CMP:
                dstSrcPair = readDstSrcPair();
                return new CmpInstruction(dstSrcPair.value1, dstSrcPair.value2);
            case JMP:
                return new JmpInstruction(JmpKind.None, readDst());
            case JMP_EQUALS:
                return new JmpInstruction(JmpKind.Equals, readDst());
            case JMP_NOTEQUALS:
                return new JmpInstruction(JmpKind.NotEquals, readDst());
            case JMP_LESS:
                return new JmpInstruction(JmpKind.Less, readDst());
            case JMP_LESS_OR_EQUALS:
                return new JmpInstruction(JmpKind.LessOrEquals, readDst());
            case JMP_GREATER:
                return new JmpInstruction(JmpKind.Greater, readDst());
            case JMP_GREATER_OR_EQUALS:
                return new JmpInstruction(JmpKind.GreaterOrEquals, readDst());
            case HALT:
                return new HaltInstruction();
            default:
                throw new IllegalArgumentException("Unexpected opcode in instruction decode: " + opcode);
        }
    }

    private Operand readDst() {
        var dst = AccessMode.fromValue(next());
        return readOperand(dst);
    }

    private Tuple<Operand, Operand> readDstSrcPair() {
        var mode = AccessMode.decode(next());
        var dst = readOperand(mode.value1);
        var src = readOperand(mode.value2);
        return new Tuple<Operand, Operand>(dst, src);
    }

    private Operand readOperand(AccessMode mode) {
        var next = next();
        return switch (mode) {
            case IMMEDIATE -> new ImmediateOperand(next);
            case REGISTER -> new RegisterOperand(next);
            default -> throw new IllegalArgumentException("Unexpected access mode to read: " + mode);
        };
    }

    private byte next() {
        return bytecode[position++];
    }
}
