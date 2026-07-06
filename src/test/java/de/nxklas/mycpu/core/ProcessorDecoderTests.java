package de.nxklas.mycpu.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static de.nxklas.mycpu.helpers.InstructionFactory.*;
import static de.nxklas.mycpu.helpers.OperandFactory.*;
import static de.nxklas.mycpu.helpers.Bytecode.*;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import de.nxklas.mycpu.core.instructions.Instruction;

public final class ProcessorDecoderTests {
    private static final Stream<Arguments> decodesCorrectInstruction_args() {
        return Stream.of(
            createInstructionArgs((byte) 0x00, (byte) 0x01),
            createInstructionArgs((byte) 0x02, (byte) 0x05),
            createInstructionArgs((byte) 0x0A, (byte) 0x7F)
        );
    }

    private static Arguments createInstructionArgs(byte dst, byte src) {
        return Arguments.of(
            ADD, IMM_TO_REG, dst, src, add(register(dst), immediate(src)),
            ADD, REG_TO_REG, dst, src, add(register(dst), register(src)),

            CMP, IMM_TO_REG, dst, src, cmp(register(dst), immediate(src)),
            CMP, REG_TO_REG, dst, src, cmp(register(dst), register(src)),

            MOV, IMM_TO_REG, dst, src, mov(register(dst), immediate(src)),
            MOV, REG_TO_REG, dst, src, mov(register(dst), register(src)),

            SUB, IMM_TO_REG, dst, src, sub(register(dst), immediate(src)),
            SUB, REG_TO_REG, dst, src, sub(register(dst), register(src))
        );
    }

    @ParameterizedTest
    @MethodSource("decodesCorrectInstruction_args")
    public void decodesCorrectInstruction(byte opcode, byte accesMode, byte dst, byte src, Instruction result) {
        var program = new byte[] {
            opcode, accesMode, dst, src
        };
        var processor = new Processor(program, 1); // To prevent the opcode being read, we set the program counter to
                                                      // the second instruction.
        var instruction = processor.decode(opcode);
        assertEquals(result, instruction);
    }
}
