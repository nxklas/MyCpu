package de.nxklas.mycpu.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static de.nxklas.mycpu.helpers.Bytecode.*;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/*
    IMMEDIATE(0b00),
    REGISTER(0b01);

    NOP(0x00),
    MOV(0x10),
    HALT(0xFF);
*/

public final class ProcessorTests {
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
            MOV, IMM_TO_REG, dstRegister, srcImmediate
        };
        var processor = new Processor(asInstructions(program));

        processor.execute();
        assertEquals(Byte.toUnsignedInt(srcImmediate), processor.peekRegister(dstRegister));
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
            MOV, IMM_TO_REG, srcRegister, immediate,
            MOV, REG_TO_REG, dstRegister, srcRegister
        };
        var processor = new Processor(asInstructions(program));

        processor.execute();
        assertEquals(Byte.toUnsignedInt(immediate), processor.peekRegister(dstRegister));
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
            MOV, IMM_TO_REG, dstRegister, dstValue,
            ADD, IMM_TO_REG, dstRegister, srcImmediate
        };
        var processor = new Processor(asInstructions(program));

        processor.execute();
        assertEquals(Byte.toUnsignedInt((byte) (dstValue + srcImmediate)), processor.peekRegister(dstRegister));
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
            MOV, IMM_TO_REG, dstRegister, dstValue,
            MOV, IMM_TO_REG, srcRegister, srcValue,
            ADD, REG_TO_REG, dstRegister, srcRegister
        };
        var processor = new Processor(asInstructions(program));

        processor.execute();
        assertEquals(Byte.toUnsignedInt((byte) (dstValue + srcValue)), processor.peekRegister(dstRegister));
    }

    @ParameterizedTest
    @MethodSource("addOrSubImmedaiteToRegisterCalculatesCorrectly_args")
    public void subImmediateToRegisterCaculatesCorrectly(byte dstRegister, byte srcImmediate, byte dstValue) {
        var program = new byte[] {
            MOV, IMM_TO_REG, dstRegister, dstValue,
            SUB, IMM_TO_REG, dstRegister, srcImmediate
        };
        var processor = new Processor(asInstructions(program));

        processor.execute();
        assertEquals(Byte.toUnsignedInt((byte) (dstValue - srcImmediate)), processor.peekRegister(dstRegister));
    }

    @ParameterizedTest
    @MethodSource("addOrSubRegisterToRegisterCalculatesCorrectly_args")
    public void subRegisterToRegisterCaculatesCorrectly(byte dstRegister, byte srcRegister, byte dstValue, byte srcValue) {
        var program = new byte[] {
            MOV, IMM_TO_REG, dstRegister, dstValue,
            MOV, IMM_TO_REG, srcRegister, srcValue,
            SUB, REG_TO_REG, dstRegister, srcRegister
        };
        var processor = new Processor(asInstructions(program));

        processor.execute();
        assertEquals(Byte.toUnsignedInt((byte) (dstValue - srcValue)), processor.peekRegister(dstRegister));
    }

    private static Stream<Arguments> cmpImmediateToRegisterSetsExpectedFalgs_args() {
        return Stream.of(
            Arguments.of((byte) 0x00, (byte) 5, (byte) 5, Alu.FLAG_ZERO),
            Arguments.of((byte) 0x01, (byte) 3, (byte) 5, Alu.FLAG_NONE),
            Arguments.of((byte) 0x02, (byte) 5, (byte) 3, (byte) (Alu.FLAG_NEG | Alu.FLAG_CARRY)),
            Arguments.of((byte) 0x03, (byte) 1, (byte) 0, (byte) (Alu.FLAG_NEG | Alu.FLAG_CARRY)),
            Arguments.of((byte) 0x04, (byte) 127, (byte) 127, Alu.FLAG_ZERO),
            Arguments.of((byte) 0x05, (byte) -128, (byte) -128, Alu.FLAG_ZERO),
            Arguments.of((byte) 0x06, (byte) -2, (byte) -1, Alu.FLAG_NONE),
            Arguments.of((byte) 0x07, (byte) -1, (byte) -2, (byte) (Alu.FLAG_NEG | Alu.FLAG_CARRY))
        );
    }

    @ParameterizedTest
    @MethodSource("cmpImmediateToRegisterSetsExpectedFalgs_args")
    public void cmpImmediateToRegisterSetsExpectedFalgs(byte dstRegister, byte srcImmediate, byte dstValue, byte expected) {
        var program = new byte[] {
            MOV, IMM_TO_REG, dstRegister, dstValue,
            CMP, IMM_TO_REG, dstRegister, srcImmediate
        };
        var processor = new Processor(asInstructions(program));

        processor.execute();
        assertEquals(expected, processor.peekFlags());
    }

    private static Stream<Arguments> cmpImmediateToImmediateSetsExpectedFlags_args() {
        return Stream.of(
            Arguments.of((byte) 0, (byte) 0, Alu.FLAG_ZERO),
            Arguments.of((byte) 5, (byte) 5, Alu.FLAG_ZERO),
            Arguments.of((byte) -1, (byte) -1, Alu.FLAG_ZERO),
            Arguments.of((byte) 5, (byte) 3, Alu.FLAG_NONE),
            Arguments.of((byte) 127, (byte) 0, Alu.FLAG_NONE),
            Arguments.of((byte) -1, (byte) -2, Alu.FLAG_NONE),
            Arguments.of((byte) 3, (byte) 5, (byte) (Alu.FLAG_NEG | Alu.FLAG_CARRY)),
            Arguments.of((byte) 0, (byte) 1, (byte) (Alu.FLAG_NEG | Alu.FLAG_CARRY)),
            Arguments.of((byte) -2, (byte) -1, (byte) (Alu.FLAG_NEG | Alu.FLAG_CARRY))
        );
    }

    @ParameterizedTest
    @MethodSource("cmpImmediateToImmediateSetsExpectedFlags_args")
    public void cmpImmediateToImmediateSetsExpectedFlags(byte left, byte right, byte expected) {
        var program = new byte[] {
            CMP, IMM_TO_IMM, left, right
        };
        var processor = new Processor(asInstructions(program));

        processor.execute();
        assertEquals(expected, processor.peekFlags());
    }

    private static Stream<Arguments> cmpRegisterToRegisterSetsExpectedFalgs_args() {
        return Stream.of(
            Arguments.of((byte) 0x01, (byte) 0x02, (byte) 0, (byte) 0, Alu.FLAG_ZERO),
            Arguments.of((byte) 0x00, (byte) 0x01, (byte) 42, (byte) 42, Alu.FLAG_ZERO),
            Arguments.of((byte) 0x02, (byte) 0x00, (byte) -1, (byte) -1, Alu.FLAG_ZERO),

            Arguments.of((byte) 0x03, (byte) 0x09, (byte) 5, (byte) 3, Alu.FLAG_NONE),
            Arguments.of((byte) 0x04, (byte) 0x08, (byte) 127, (byte) 126, Alu.FLAG_NONE),
            Arguments.of((byte) 0x05, (byte) 0x07, (byte) -1, (byte) -2, Alu.FLAG_NONE),

            Arguments.of((byte) 0x06, (byte) 0x05, (byte) 3, (byte) 5, (byte) (Alu.FLAG_NEG | Alu.FLAG_CARRY)),
            Arguments.of((byte) 0x07, (byte) 0x05, (byte) 0, (byte) 1, (byte) (Alu.FLAG_NEG | Alu.FLAG_CARRY)),
            Arguments.of((byte) 0x08, (byte) 0x04, (byte) -2, (byte) -1, (byte) (Alu.FLAG_NEG | Alu.FLAG_CARRY)),

            Arguments.of((byte) 0x09, (byte) 0x03, (byte) 0xFF, (byte) 0xFF, Alu.FLAG_ZERO),
            Arguments.of((byte) 0x00, (byte) 0x02, (byte) 0x80, (byte) 0x80, Alu.FLAG_ZERO),
            Arguments.of((byte) 0x01, (byte) 0x00, (byte) 0x80, (byte) 0xFF, (byte) (Alu.FLAG_NEG | Alu.FLAG_CARRY)),
            Arguments.of((byte) 0x02, (byte) 0x01, (byte) 0xFF, (byte) 0x80, Alu.FLAG_NONE),

            Arguments.of((byte) 0x03, (byte) 0x02, (byte) 0x12, (byte) 0x12, Alu.FLAG_ZERO),
            Arguments.of((byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) (Alu.FLAG_NEG | Alu.FLAG_CARRY)),
            Arguments.of((byte) 0x01, (byte) 0x02, (byte) 0x10, (byte) 0x11, (byte) (Alu.FLAG_NEG | Alu.FLAG_CARRY)),
            Arguments.of((byte) 0x02, (byte) 0x01, (byte) 0xFF, (byte) 0x01, Alu.FLAG_NEG),
            Arguments.of((byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, Alu.FLAG_ZERO),
            Arguments.of((byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, Alu.FLAG_NONE)
        );
    }

    @ParameterizedTest
    @MethodSource("cmpRegisterToRegisterSetsExpectedFalgs_args")
    public void cmpRegisterToRegisterSetsExpectedFalgs(byte dstRegister, byte srcRegister, byte dstValue, byte srcValue, byte expected) {
        var program = new byte[] {
            MOV, IMM_TO_REG, dstRegister, dstValue,
            MOV, IMM_TO_REG, srcRegister, srcValue,
            CMP, REG_TO_REG, dstRegister, srcRegister
        };
        var processor = new Processor(asInstructions(program));

        processor.execute();
        assertEquals(expected, processor.peekFlags());
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
        byte jmpTo = 0x02;
        var program = new byte[] {
            CMP, IMM_TO_IMM, cmpA, cmpA,
            JMP_EQUALS, IMM, jmpTo,
            HALT
        };
        var processor = new Processor(asInstructions(program));

        processor.execute();
        assertEquals(jmpTo + 1, processor.peekProgramCounter()); // We need to add jmpTo by 1, because in the last
                                                                 // execute iteration when HALT gets decoded, the call
                                                                 // of next() auto-increments the pc.
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
            CMP, IMM_TO_IMM, cmpA, cmpB,
            JMP_EQUALS, IMM, jmpTo,
            HALT
        };
        var processor = new Processor(asInstructions(program));

        processor.execute();
        assertNotEquals(jmpTo + 1, processor.peekProgramCounter());
        assertEquals(3, processor.peekProgramCounter());
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
        byte jmpTo = 0x02;
        var program = new byte[] {
            CMP, IMM_TO_IMM, cmpA, cmpB,
            JMP_NOTEQUALS, IMM, jmpTo,
            HALT
        };
        var processor = new Processor(asInstructions(program));

        processor.execute();
        assertEquals(jmpTo + 1, processor.peekProgramCounter());
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
            CMP, IMM_TO_IMM, cmpA, cmpA,
            JMP_NOTEQUALS, IMM, jmpTo,
            HALT
        };
        var processor = new Processor(asInstructions(program));

        processor.execute();
        assertNotEquals(jmpTo + 1, processor.peekProgramCounter());
        assertEquals(3, processor.peekProgramCounter());
    }

    private static final Stream<Arguments> jmpLess_args() {
        return Stream.of(
            Arguments.of((byte) 0x00, (byte) 0x01),
            Arguments.of((byte) 0x06, (byte) 0x08),
            Arguments.of((byte) 0x02, (byte) 0x03)
        );
    }

    @ParameterizedTest
    @MethodSource("jmpLess_args")
    public void jmpLess(byte cmpA, byte cmpB) {
        byte jmpTo = 0x02;
        var program = new byte[] {
            CMP, IMM_TO_IMM, cmpA, cmpB,
            JMP_LESS, IMM, jmpTo,
            HALT
        };
        var processor = new Processor(asInstructions(program));

        processor.execute();
        assertEquals(jmpTo + 1, processor.peekProgramCounter());
    }

    private static final Stream<Arguments> jumpLessFails_args() {
        return Stream.of(
            Arguments.of((byte) 0x01, (byte) 0x01),
            Arguments.of((byte) 0x09, (byte) 0x08),
            Arguments.of((byte) 0x03, (byte) 0x03)
        );
    }

    @ParameterizedTest
    @MethodSource("jumpLessFails_args")
    public void jumpLessFails(byte cmpA, byte cmpB) {
        byte jmpTo = 0x00;
        var program = new byte[] {
            CMP, IMM_TO_IMM, cmpA, cmpB,
            JMP_LESS, IMM, jmpTo,
            HALT
        };
        var processor = new Processor(asInstructions(program));

        processor.execute();
        assertNotEquals(jmpTo + 1, processor.peekProgramCounter());
        assertEquals(3, processor.peekProgramCounter());
    }

    private static final Stream<Arguments> jmpLessEquals_args() {
        return Stream.of(
            Arguments.of((byte) 0x01, (byte) 0x01),
            Arguments.of((byte) 0x07, (byte) 0x08),
            Arguments.of((byte) 0x03, (byte) 0x03)
        );
    }

    @ParameterizedTest
    @MethodSource("jmpLessEquals_args")
    public void jmpLessEquals(byte cmpA, byte cmpB) {
        byte jmpTo = 0x02;
        var program = new byte[] {
            CMP, IMM_TO_IMM, cmpA, cmpB,
            JMP_LESS_OR_EQUALS, IMM, jmpTo,
            HALT
        };
        var processor = new Processor(asInstructions(program));

        processor.execute();
        assertEquals(jmpTo + 1, processor.peekProgramCounter());
    }

    private static final Stream<Arguments> jmpLessEqualsFails_args() {
        return Stream.of(
            Arguments.of((byte) 0x02, (byte) 0x01),
            Arguments.of((byte) 0x09, (byte) 0x08),
            Arguments.of((byte) 0x05, (byte) 0x03)
        );
    }

    @ParameterizedTest
    @MethodSource("jmpLessEqualsFails_args")
    public void jmpLessEqualsFails(byte cmpA, byte cmpB) {
        byte jmpTo = 0x00;
        var program = new byte[] {
            CMP, IMM_TO_IMM, cmpA, cmpB,
            JMP_LESS_OR_EQUALS, IMM, jmpTo,
            HALT
        };
        var processor = new Processor(asInstructions(program));

        processor.execute();
        assertNotEquals(jmpTo + 1, processor.peekProgramCounter());
        assertEquals(3, processor.peekProgramCounter());
    }

    private static final Stream<Arguments> jmpGreater_args() {
        return Stream.of(
            Arguments.of((byte) 0x02, (byte) 0x01),
            Arguments.of((byte) 0x09, (byte) 0x08),
            Arguments.of((byte) 0x05, (byte) 0x03)
        );
    }

    @ParameterizedTest
    @MethodSource("jmpGreater_args")
    public void jmpGreater(byte cmpA, byte cmpB) {
        byte jmpTo = 0x02;
        var program = new byte[] {
            CMP, IMM_TO_IMM, cmpA, cmpB,
            JMP_GREATER, IMM, jmpTo,
            HALT
        };
        var processor = new Processor(asInstructions(program));

        processor.execute();
        assertEquals(jmpTo + 1, processor.peekProgramCounter());
    }

    private static final Stream<Arguments> jmpGreaterFails_args() {
        return Stream.of(
            Arguments.of((byte) 0x01, (byte) 0x01),
            Arguments.of((byte) 0x09, (byte) 0x08),
            Arguments.of((byte) 0x05, (byte) 0x03)
        );
    }

    @ParameterizedTest
    @MethodSource("jmpGreaterFails_args")
    public void jmpGreaterFails(byte cmpA, byte cmpB) {
        byte jmpTo = 0x00;
        var program = new byte[] {
            CMP, IMM_TO_IMM, cmpA, cmpB,
            JMP_GREATER, IMM, jmpTo,
            HALT
        };
        var processor = new Processor(asInstructions(program));

        processor.execute();
        assertNotEquals(jmpTo + 1, processor.peekProgramCounter());
        assertEquals(3, processor.peekProgramCounter());
    }

    private static final Stream<Arguments> jmpGreaterEquals_args() {
        return Stream.of(
            Arguments.of((byte) 0x02, (byte) 0x01),
            Arguments.of((byte) 0x08, (byte) 0x08),
            Arguments.of((byte) 0x05, (byte) 0x03)
        );
    }

    @ParameterizedTest
    @MethodSource("jmpGreaterEquals_args")
    public void jmpGreaterEquals(byte cmpA, byte cmpB) {
        byte jmpTo = 0x02;
        var program = new byte[] {
            CMP, IMM_TO_IMM, cmpA, cmpB,
            JMP_GREATER_OR_EQUALS, IMM, jmpTo,
            HALT
        };
        var processor = new Processor(asInstructions(program));

        processor.execute();
        assertEquals(jmpTo + 1, processor.peekProgramCounter());
    }

    private static final Stream<Arguments> jmpGreaterEqualsFails_args() {
        return Stream.of(
            Arguments.of((byte) 0x02, (byte) 0x03),
            Arguments.of((byte) 0x06, (byte) 0x08),
            Arguments.of((byte) 0x05, (byte) 0x06)
        );
    }

    @ParameterizedTest
    @MethodSource("jmpGreaterEqualsFails_args")
    public void jmpGreaterEqualsFails(byte cmpA, byte cmpB) {
        byte jmpTo = 0x00;
        var program = new byte[] {
            CMP, IMM_TO_IMM, cmpA, cmpB,
            JMP_GREATER_OR_EQUALS, IMM, jmpTo,
            HALT
        };
        var processor = new Processor(asInstructions(program));

        processor.execute();
        assertNotEquals(jmpTo + 1, processor.peekProgramCounter());
        assertEquals(3, processor.peekProgramCounter());
    }
}
