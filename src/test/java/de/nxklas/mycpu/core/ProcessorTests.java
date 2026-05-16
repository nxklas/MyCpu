package de.nxklas.mycpu.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static de.nxklas.mycpu.helpers.InstructionFactory.*;
import static de.nxklas.mycpu.helpers.OperandFactory.*;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import de.nxklas.mycpu.core.instructions.Instruction;
import de.nxklas.mycpu.core.operands.ImmediateOperand;
import de.nxklas.mycpu.core.operands.RegisterOperand;

/*
    IMMEDIATE(0b00),
    REGISTER(0b01);

    NOP(0x00),
    MOV(0x10),
    HALT(0xFF);
*/

public class ProcessorTests {
    private static final byte IMM_TO_REG = AccessMode.encode(AccessMode.REGISTER, AccessMode.IMMEDIATE);
    private static final byte REG_TO_REG = AccessMode.encode(AccessMode.REGISTER, AccessMode.REGISTER);

    private static final Stream<Arguments> decodeDecodesInstructionCorrectly_args() {
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
    @MethodSource("decodeDecodesInstructionCorrectly_args")
    public void decodeDecodesInstructionCorrectly(Opcode opcode, byte accesMode, byte dst, byte src, Instruction result) {
        var program = new byte[] {
            opcode.value, accesMode, dst, src
        };
        var processor = new Processor(program, 1); // To prevent the opcode being read, we set the program counter to the second instruction.
        var instruction = processor.decode(opcode.value);
        assertEquals(instruction, result);
    }

    private static Stream<Arguments> resolveResolvesImmediateValueCorrectly_args() {
        return Stream.of(
            Arguments.of(immediate(0x00), 0x00),
            Arguments.of(immediate(0x10), 0x10)
        );
    }

    @ParameterizedTest
    @MethodSource("resolveResolvesImmediateValueCorrectly_args")
    public void resolveResolvesImmediateValueCorrectly(ImmediateOperand operand, int value) {
        var processor = new Processor(null);
        var opVal = processor.resolve(operand);
        assertEquals(opVal, value);
    }

    private static Stream<Arguments> resolveResolvesRegisterValueCorrectly_args() {
        return Stream.of(
            Arguments.of(register(0x00), 0x00, 0x20),
            Arguments.of(register(0x01), 0x01, 0x10)
        );
    }

    @ParameterizedTest
    @MethodSource("resolveResolvesRegisterValueCorrectly_args")
    public void resolveResolvesRegisterValueCorrectly(RegisterOperand operand, int index, int value) {
        var processor = new Processor(null);
        processor.writeRegister(index, value);
        var opVal = processor.resolve(operand);
        assertEquals(opVal, value);
    }

    private static final Stream<Arguments> movImmediateToRegisterExecutesCorrectly_args() {
        return Stream.of(
            Arguments.of((byte) 0x00, (byte) 0x10),
            Arguments.of((byte) 0x01, (byte) 0x11),
            Arguments.of((byte) 0x02, (byte) 0x12),
            Arguments.of((byte) 0x03, (byte) 0x13),
            Arguments.of((byte) 0x04, (byte) 0x14),
            Arguments.of((byte) 0x05, (byte) 0x15),
            Arguments.of((byte) 0x06, (byte) 0x16),
            Arguments.of((byte) 0x07, (byte) 0x17),
            Arguments.of((byte) 0x08, (byte) 0x18),
            Arguments.of((byte) 0x09, (byte) 0x19)
        );
    }

    @ParameterizedTest
    @MethodSource("movImmediateToRegisterExecutesCorrectly_args")
    public void movImmediateToRegisterExecutesCorrectly(byte dstRegister, byte srcImmediate) {
        var program = new byte[] {
            Opcode.MOV.value, IMM_TO_REG, dstRegister, srcImmediate
        };
        var processor = new Processor(program);

        processor.execute();
        assertEquals(processor.peekRegister(dstRegister), srcImmediate);
    }

    private static final Stream<Arguments> movRegisterToRegisterExecutesCorrectly_args() {
        return Stream.of(
            Arguments.of((byte) 0x00, (byte) 0x09, (byte) 0x10),
            Arguments.of((byte) 0x01, (byte) 0x08, (byte) 0x11),
            Arguments.of((byte) 0x02, (byte) 0x07, (byte) 0x12),
            Arguments.of((byte) 0x03, (byte) 0x06, (byte) 0x13),
            Arguments.of((byte) 0x04, (byte) 0x05, (byte) 0x14),
            Arguments.of((byte) 0x05, (byte) 0x04, (byte) 0x15),
            Arguments.of((byte) 0x06, (byte) 0x03, (byte) 0x16),
            Arguments.of((byte) 0x07, (byte) 0x02, (byte) 0x17),
            Arguments.of((byte) 0x08, (byte) 0x01, (byte) 0x18),
            Arguments.of((byte) 0x09, (byte) 0x00, (byte) 0x19)
        );
    }

    @ParameterizedTest
    @MethodSource("movRegisterToRegisterExecutesCorrectly_args")
    public void movRegisterToRegisterExecutesCorrectly(byte dstRegister, byte srcRegister, byte immediate) {
        var program = new byte[] {
            Opcode.MOV.value, IMM_TO_REG, srcRegister, immediate,
            Opcode.MOV.value, REG_TO_REG, dstRegister, srcRegister
        };
        var processor = new Processor(program);

        processor.execute();
        assertEquals(processor.peekRegister(dstRegister), immediate);
    }

    private static final Stream<Arguments> addOrSubImmedaiteToRegisterCalculatesCorrectly_args() {
        return Stream.of(
            Arguments.of((byte) 0x00, (byte) 0x09, (byte) 0x10),
            Arguments.of((byte) 0x01, (byte) 0x08, (byte) 0x11),
            Arguments.of((byte) 0x02, (byte) 0x07, (byte) 0x12),
            Arguments.of((byte) 0x03, (byte) 0x06, (byte) 0x13),
            Arguments.of((byte) 0x04, (byte) 0x05, (byte) 0x14),
            Arguments.of((byte) 0x05, (byte) 0x04, (byte) 0x15),
            Arguments.of((byte) 0x06, (byte) 0x03, (byte) 0x16),
            Arguments.of((byte) 0x07, (byte) 0x02, (byte) 0x17),
            Arguments.of((byte) 0x08, (byte) 0x01, (byte) 0x18),
            Arguments.of((byte) 0x09, (byte) 0x00, (byte) 0x19)
        );
    }

    @ParameterizedTest
    @MethodSource("addOrSubImmedaiteToRegisterCalculatesCorrectly_args")
    public void addImmediateToRegisterCaculatesCorrectly(byte dstRegister, byte srcImmediate, byte dstValue) {
        var program = new byte[] {
            Opcode.MOV.value, IMM_TO_REG, dstRegister, dstValue,
            Opcode.ADD.value, IMM_TO_REG, dstRegister, srcImmediate
        };
        var processor = new Processor(program);

        processor.execute();
        assertEquals(processor.peekRegister(dstRegister), dstValue + srcImmediate);
    }

    private static final Stream<Arguments> addOrSubRegisterToRegisterCalculatesCorrectly_args() {
        return Stream.of(
            Arguments.of((byte) 0x00, (byte) 0x09, (byte) 0x10, (byte) 0x19),
            Arguments.of((byte) 0x01, (byte) 0x08, (byte) 0x11, (byte) 0x18),
            Arguments.of((byte) 0x02, (byte) 0x07, (byte) 0x12, (byte) 0x17),
            Arguments.of((byte) 0x03, (byte) 0x06, (byte) 0x13, (byte) 0x16),
            Arguments.of((byte) 0x04, (byte) 0x05, (byte) 0x14, (byte) 0x15),
            Arguments.of((byte) 0x05, (byte) 0x04, (byte) 0x15, (byte) 0x14),
            Arguments.of((byte) 0x06, (byte) 0x03, (byte) 0x16, (byte) 0x13),
            Arguments.of((byte) 0x07, (byte) 0x02, (byte) 0x17, (byte) 0x12),
            Arguments.of((byte) 0x08, (byte) 0x01, (byte) 0x18, (byte) 0x11),
            Arguments.of((byte) 0x09, (byte) 0x00, (byte) 0x19, (byte) 0x10)
        );
    }

    @ParameterizedTest
    @MethodSource("addOrSubRegisterToRegisterCalculatesCorrectly_args")
    public void addRegisterToRegisterCaculatesCorrectly(byte dstRegister, byte srcRegister, byte dstValue, byte srcValue) {
        var program = new byte[] {
            Opcode.MOV.value, IMM_TO_REG, dstRegister, dstValue,
            Opcode.MOV.value, IMM_TO_REG, srcRegister, srcValue,
            Opcode.ADD.value, REG_TO_REG, dstRegister, srcRegister
        };
        var processor = new Processor(program);

        processor.execute();
        assertEquals(processor.peekRegister(dstRegister), dstValue + srcValue);
    }

    @ParameterizedTest
    @MethodSource("addOrSubImmedaiteToRegisterCalculatesCorrectly_args")
    public void subImmediateToRegisterCaculatesCorrectly(byte dstRegister, byte srcImmediate, byte dstValue) {
        var program = new byte[] {
            Opcode.MOV.value, IMM_TO_REG, dstRegister, dstValue,
            Opcode.SUB.value, IMM_TO_REG, dstRegister, srcImmediate
        };
        var processor = new Processor(program);

        processor.execute();
        assertEquals(processor.peekRegister(dstRegister), dstValue - srcImmediate);
    }

    @ParameterizedTest
    @MethodSource("addOrSubRegisterToRegisterCalculatesCorrectly_args")
    public void subRegisterToRegisterCaculatesCorrectly(byte dstRegister, byte srcRegister, byte dstValue, byte srcValue) {
        var program = new byte[] {
            Opcode.MOV.value, IMM_TO_REG, dstRegister, dstValue,
            Opcode.MOV.value, IMM_TO_REG, srcRegister, srcValue,
            Opcode.SUB.value, REG_TO_REG, dstRegister, srcRegister
        };
        var processor = new Processor(program);

        processor.execute();
        assertEquals(processor.peekRegister(dstRegister), dstValue - srcValue);
    }

    private static final Stream<Arguments> cmpImmediateToRegisterSetsExpectedFalgs_args() {
        return Stream.of(
            Arguments.of((byte) 0x00, (byte) 0x09, (byte) 0x10, Alu.FLAG_NONE),
            Arguments.of((byte) 0x01, (byte) 0x08, (byte) 0x11, Alu.FLAG_NONE),
            Arguments.of((byte) 0x02, (byte) 0x07, (byte) 0x12, Alu.FLAG_NONE),
            Arguments.of((byte) 0x03, (byte) 0x06, (byte) 0x13, Alu.FLAG_NONE),
            Arguments.of((byte) 0x04, (byte) 0x05, (byte) 0x14, Alu.FLAG_NONE),
            Arguments.of((byte) 0x05, (byte) 0x04, (byte) 0x15, Alu.FLAG_NONE),
            Arguments.of((byte) 0x06, (byte) 0x03, (byte) 0x16, Alu.FLAG_NONE),
            Arguments.of((byte) 0x07, (byte) 0x02, (byte) 0x17, Alu.FLAG_NONE),
            Arguments.of((byte) 0x08, (byte) 0x01, (byte) 0x18, Alu.FLAG_NONE),
            Arguments.of((byte) 0x09, (byte) 0x00, (byte) 0x19, Alu.FLAG_NONE),

            Arguments.of((byte) 0x00, (byte) 0x10, (byte) 0x09, Alu.FLAG_NEG),
            Arguments.of((byte) 0x01, (byte) 0x11, (byte) 0x08, Alu.FLAG_NEG),
            Arguments.of((byte) 0x02, (byte) 0x12, (byte) 0x07, Alu.FLAG_NEG),
            Arguments.of((byte) 0x03, (byte) 0x13, (byte) 0x06, Alu.FLAG_NEG),
            Arguments.of((byte) 0x04, (byte) 0x14, (byte) 0x05, Alu.FLAG_NEG),
            Arguments.of((byte) 0x05, (byte) 0x15, (byte) 0x04, Alu.FLAG_NEG),
            Arguments.of((byte) 0x06, (byte) 0x16, (byte) 0x03, Alu.FLAG_NEG),
            Arguments.of((byte) 0x07, (byte) 0x17, (byte) 0x02, Alu.FLAG_NEG),
            Arguments.of((byte) 0x08, (byte) 0x18, (byte) 0x01, Alu.FLAG_NEG),
            Arguments.of((byte) 0x09, (byte) 0x19, (byte) 0x00, Alu.FLAG_NEG),

            Arguments.of((byte) 0x00, (byte) 0x09, (byte) 0x09, Alu.FLAG_ZERO),
            Arguments.of((byte) 0x01, (byte) 0x08, (byte) 0x08, Alu.FLAG_ZERO),
            Arguments.of((byte) 0x02, (byte) 0x07, (byte) 0x07, Alu.FLAG_ZERO),
            Arguments.of((byte) 0x03, (byte) 0x06, (byte) 0x06, Alu.FLAG_ZERO),
            Arguments.of((byte) 0x04, (byte) 0x05, (byte) 0x05, Alu.FLAG_ZERO),
            Arguments.of((byte) 0x05, (byte) 0x04, (byte) 0x04, Alu.FLAG_ZERO),
            Arguments.of((byte) 0x06, (byte) 0x03, (byte) 0x03, Alu.FLAG_ZERO),
            Arguments.of((byte) 0x07, (byte) 0x02, (byte) 0x02, Alu.FLAG_ZERO),
            Arguments.of((byte) 0x08, (byte) 0x01, (byte) 0x01, Alu.FLAG_ZERO),
            Arguments.of((byte) 0x09, (byte) 0x00, (byte) 0x00, Alu.FLAG_ZERO)
        );
    }

    @ParameterizedTest
    @MethodSource("cmpImmediateToRegisterSetsExpectedFalgs_args")
    public void cmpImmediateToRegisterSetsExpectedFalgs(byte dstRegister, byte srcImmediate, byte dstValue, byte expected) {
        var program = new byte[] {
                Opcode.MOV.value, IMM_TO_REG, dstRegister, dstValue,
                Opcode.CMP.value, IMM_TO_REG, dstRegister, srcImmediate
        };
        var processor = new Processor(program);

        processor.execute();
        assertEquals(processor.peekFlags(), expected);
    }

    private static final Stream<Arguments> cmpRegisterToRegisterSetsExpectedFalgs_args() {
        return Stream.of(
            Arguments.of((byte) 0x00, (byte) 0x09, (byte) 0x10, (byte) 0x00, Alu.FLAG_NONE),
            Arguments.of((byte) 0x01, (byte) 0x08, (byte) 0x11, (byte) 0x01, Alu.FLAG_NONE),
            Arguments.of((byte) 0x02, (byte) 0x07, (byte) 0x12, (byte) 0x02, Alu.FLAG_NONE),
            Arguments.of((byte) 0x03, (byte) 0x06, (byte) 0x13, (byte) 0x03, Alu.FLAG_NONE),
            Arguments.of((byte) 0x04, (byte) 0x05, (byte) 0x14, (byte) 0x04, Alu.FLAG_NONE),
            Arguments.of((byte) 0x05, (byte) 0x04, (byte) 0x15, (byte) 0x05, Alu.FLAG_NONE),
            Arguments.of((byte) 0x06, (byte) 0x03, (byte) 0x16, (byte) 0x06, Alu.FLAG_NONE),
            Arguments.of((byte) 0x07, (byte) 0x02, (byte) 0x17, (byte) 0x07, Alu.FLAG_NONE),
            Arguments.of((byte) 0x08, (byte) 0x01, (byte) 0x18, (byte) 0x08, Alu.FLAG_NONE),
            Arguments.of((byte) 0x09, (byte) 0x00, (byte) 0x19, (byte) 0x09, Alu.FLAG_NONE)
        );
    }

    @ParameterizedTest
    @MethodSource("cmpRegisterToRegisterSetsExpectedFalgs_args")
    public void cmpRegisterToRegisterSetsExpectedFalgs(byte dstRegister, byte srcRegister, byte dstValue, byte srcValue, byte expected) {
        var program = new byte[] {
            Opcode.MOV.value, IMM_TO_REG, dstRegister, dstValue,
            Opcode.MOV.value, IMM_TO_REG, srcRegister, srcValue,
            Opcode.CMP.value, IMM_TO_REG, dstRegister, srcRegister
        };
        var processor = new Processor(program);

        processor.execute();
        assertEquals(processor.peekFlags(), expected);
    }
}
