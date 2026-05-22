package de.nxklas.mycpu.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import de.nxklas.mycpu.helpers.AccessModes;

/*
    IMMEDIATE(0b00),
    REGISTER(0b01);

    NOP(0x00),
    MOV(0x10),
    HALT(0xFF);
*/

public final class ProcessorTests implements AccessModes {
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

    private static final Stream<Arguments> jmpEquals_args() {
        return Stream.of(
            Arguments.of((byte) 0x00),
            Arguments.of((byte) 0x09),
            Arguments.of((byte) 0x02)
        );
    }

    @ParameterizedTest
    @MethodSource("jmpEquals_args")
    public void jmpEquals(byte cmpA) {
        byte jmpTo = 0x07;
        var program = new byte[] {
            Opcode.CMP.value, IMM_TO_IMM, cmpA, cmpA,
            Opcode.JMP_EQUALS.value, IMM, jmpTo,
            Opcode.HALT.value
        };
        var processor = new Processor(program);

        processor.execute();
        assertEquals(processor.peekProgramCounter(), jmpTo + 1); // We need to add jmpTo by 1, because in the last execute iteration when HALT gets decoded, the call of next() auto-increments the pc.
    }

    private static final Stream<Arguments> jmpEqualsFails_args() {
        return Stream.of(
            Arguments.of((byte) 0x00, (byte) 0x01),
            Arguments.of((byte) 0x09, (byte) 0x08),
            Arguments.of((byte) 0x01, (byte) 0x03)
        );
    }

    @ParameterizedTest
    @MethodSource("jmpEqualsFails_args")
    public void jmpEqualsFails(byte cmpA, byte cmpB) {
        byte jmpTo = 0x00;
        var program = new byte[] {
            Opcode.CMP.value, IMM_TO_IMM, cmpA, cmpB,
            Opcode.JMP_EQUALS.value, IMM, jmpTo,
            Opcode.HALT.value
        };
        var processor = new Processor(program);

        processor.execute();
        assertNotEquals(processor.peekProgramCounter(), jmpTo + 1); // We need to add jmpTo by 1, because in the last execute iteration when HALT gets decoded, the call of next() auto-increments the pc.
    }

    private static final Stream<Arguments> jmpNotEquals_args() {
        return Stream.of(
            Arguments.of((byte) 0x00, (byte) 0x01),
            Arguments.of((byte) 0x09, (byte) 0x08),
            Arguments.of((byte) 0x01, (byte) 0x03)
        );
    }

    @ParameterizedTest
    @MethodSource("jmpNotEquals_args")
    public void jmpNotEquals(byte cmpA, byte cmpB) {
        byte jmpTo = 0x07;
        var program = new byte[] {
            Opcode.CMP.value, IMM_TO_IMM, cmpA, cmpB,
            Opcode.JMP_NOTEQUALS.value, IMM, jmpTo,
            Opcode.HALT.value
        };
        var processor = new Processor(program);

        processor.execute();
        assertEquals(processor.peekProgramCounter(), jmpTo + 1); // We need to add jmpTo by 1, because in the last execute iteration when HALT gets decoded, the next() call auto-increments the pc.
    }

    private static final Stream<Arguments> jmpNotEqualsFails_args() {
        return Stream.of(
            Arguments.of((byte) 0x00),
            Arguments.of((byte) 0x09),
            Arguments.of((byte) 0x02)
        );
    }

    @ParameterizedTest
    @MethodSource("jmpNotEqualsFails_args")
    public void jmpNotEqualsFails(byte cmpA) {
        byte jmpTo = 0x00;
        var program = new byte[] {
            Opcode.CMP.value, IMM_TO_IMM, cmpA, cmpA,
            Opcode.JMP_NOTEQUALS.value, IMM, jmpTo,
            Opcode.HALT.value
        };
        var processor = new Processor(program);

        processor.execute();
        assertNotEquals(processor.peekProgramCounter(), jmpTo + 1); // We need to add jmpTo by 1, because in the last execute iteration when HALT gets decoded, the next() call auto-increments the pc.
    }
}
