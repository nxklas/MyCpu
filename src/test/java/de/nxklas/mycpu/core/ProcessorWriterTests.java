package de.nxklas.mycpu.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static de.nxklas.mycpu.helpers.OperandFactory.*;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import de.nxklas.mycpu.core.operands.ImmediateOperand;
import de.nxklas.mycpu.core.operands.RegisterOperand;

public final class ProcessorWriterTests {
    private static Stream<Arguments> throwsOnImmediate_args() {
        return Stream.of(
            Arguments.of(immediate(0x00))
        );
    }

    @ParameterizedTest
    @MethodSource("throwsOnImmediate_args")
    public void throwsOnImmediate(ImmediateOperand operand) {
        var processor = new Processor(null);
        assertThrows(IllegalArgumentException.class, () -> processor.write(operand, operand.value()),
                "Cannot write to immediate, since writing to is only permitted to registers. Dst operand: "
                        + operand + "Src value: " + operand.value());
    }

    private static Stream<Arguments> writesCorrectRegister_args() {
        return Stream.of(
            Arguments.of(register(0x00), 0x00, 0x10)
        );
    }

    @ParameterizedTest
    @MethodSource("writesCorrectRegister_args")
    public void writesCorrectRegister(RegisterOperand operand, int index, int value) {
        var processor = new Processor(null);
        processor.write(operand, value);
        var writtenVal = processor.peekRegister(index);
        assertEquals(writtenVal, value);
    }
}
