package de.nxklas.mycpu.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static de.nxklas.mycpu.helpers.OperandFactory.*;


import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import de.nxklas.mycpu.core.operands.ImmediateOperand;
import de.nxklas.mycpu.core.operands.RegisterOperand;

public final class ProcessorResolverTests {
    private static Stream<Arguments> resolvesCorrectImmediateValue_args() {
        return Stream.of(
            Arguments.of(immediate(0x00), 0x00),
            Arguments.of(immediate(0x10), 0x10)
        );
    }

    @ParameterizedTest
    @MethodSource("resolvesCorrectImmediateValue_args")
    public void resolvesCorrectImmediateValue(ImmediateOperand operand, int value) {
        var processor = new Processor(null);
        var opVal = processor.resolve(operand);
        assertEquals(opVal, value);
    }

    private static Stream<Arguments> resolvesCorrectRegisterValue_args() {
        return Stream.of(
            Arguments.of(register(0x00), 0x00, 0x20),
            Arguments.of(register(0x01), 0x01, 0x10)
        );
    }

    @ParameterizedTest
    @MethodSource("resolvesCorrectRegisterValue_args")
    public void resolvesCorrectRegisterValue(RegisterOperand operand, int index, int value) {
        var processor = new Processor(null);
        processor.writeRegister(index, value);
        var opVal = processor.resolve(operand);
        assertEquals(opVal, value);
    }
}
