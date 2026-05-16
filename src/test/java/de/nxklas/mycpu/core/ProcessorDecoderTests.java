package de.nxklas.mycpu.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static de.nxklas.mycpu.helpers.InstructionFactory.*;
import static de.nxklas.mycpu.helpers.OperandFactory.*;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import de.nxklas.mycpu.core.instructions.Instruction;
import de.nxklas.mycpu.helpers.AccessModes;

public final class ProcessorDecoderTests implements AccessModes {
    private static final Stream<Arguments> decodesCorrectInstruction_args() {
        return Stream.of(
            createInstructionArgs((byte) 0x00, (byte) 0x01),
            createInstructionArgs((byte) 0x02, (byte) 0x05),
            createInstructionArgs((byte) 0x0A, (byte) 0x7F)
        );
    }

    private static Arguments createInstructionArgs(byte dst, byte src) {
        return Arguments.of(
            Opcode.ADD, IMM_TO_REG, dst, src, add(register(dst), immediate(src)),
            Opcode.ADD, REG_TO_REG, dst, src, add(register(dst), register(src)),

            Opcode.CMP, IMM_TO_REG, dst, src, cmp(register(dst), immediate(src)),
            Opcode.CMP, REG_TO_REG, dst, src, cmp(register(dst), register(src)),

            Opcode.MOV, IMM_TO_REG, dst, src, mov(register(dst), immediate(src)),
            Opcode.MOV, REG_TO_REG, dst, src, mov(register(dst), register(src)),

            Opcode.SUB, IMM_TO_REG, dst, src, sub(register(dst), immediate(src)),
            Opcode.SUB, REG_TO_REG, dst, src, sub(register(dst), register(src))
        );
    }

    @ParameterizedTest
    @MethodSource("decodesCorrectInstruction_args")
    public void decodesCorrectInstruction(Opcode opcode, byte accesMode, byte dst, byte src, Instruction result) {
        var program = new byte[] {
            opcode.value, accesMode, dst, src
        };
        var processor = new Processor(program, 1); // To prevent the opcode being read, we set the program counter to the second instruction.
        var instruction = processor.decode(opcode.value);
        assertEquals(instruction, result);
    }
}
