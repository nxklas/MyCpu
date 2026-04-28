package de.nxklas.mycpu.core;

import de.nxklas.mycpu.core.instructions.*;
import de.nxklas.mycpu.core.operands.*;

public class Processor {
    private final byte[] program;
    private final int[] registers;
    private boolean isRunning;
    private int pc;

    public Processor(byte[] program) {
        this.program = program;
        this.registers = new int[10];
        this.isRunning = false;
        this.pc = 0;
    }

    public void execute() {
        isRunning = true;
        while (isRunning && pc < program.length) {
            var instrcution = fetch();
            execute(instrcution);
        }

        if (pc > program.length)
            throw new IllegalArgumentException("Program has never ended properely.");
    }

    private Instruction fetch() {
        var opcode = next();
        return decode(opcode);
    }

    private Instruction decode(byte opcode) {
        switch (Opcode.fromValue(opcode)) {
            case NOP:
                return new NopInstruction();
            case MOV:
                var mode = AccessMode.decode(next());
                var dst = readOperand(mode.value1);
                var src = readOperand(mode.value2);
                return new MovInstruction(dst, src);
            case HALT:
                return new HaltInstruction();
            default:
                throw new IllegalArgumentException("Unexpected opcode in instruction decode: " + opcode);
        }
    }

    private void execute(Instruction instruction) {
        switch (instruction) {
            case NopInstruction _ -> nop();
            case MovInstruction movInstruction -> mov(movInstruction);
            case HaltInstruction _ -> halt();
        }
    }

    private void nop() {
    }

    private void mov(MovInstruction instruction) {
        var srcValue = resolve(instruction.src);
        write(instruction.dst, srcValue);
    }

    private void halt() {
        isRunning = false;
    }

    private byte next() {
        return program[pc++];
    }

    private Operand readOperand(AccessMode mode) {
        var next = next();
        return switch (mode) {
            case IMMEDIATE -> new ImmediateOperand(next);
            case REGISTER -> new RegisterOperand(next);
            default -> throw new IllegalArgumentException("Unexpected access mode to read: " + mode);
        };
    }

    private int resolve(Operand op) {
        return switch (op) {
            case ImmediateOperand i -> i.value;
            case RegisterOperand i -> i.index;
            default -> throw new IllegalArgumentException("Unexpected operand to resolve: " + op);
        };
    }

    private void write(Operand dst, int value) {
        switch (dst) {
            case ImmediateOperand _ -> throw new IllegalArgumentException(
                    "Cannot write to immediate, since writing to is only permitted to registers. Dst operand: "
                            + dst + "Src value: " + value);
            case RegisterOperand register -> registers[register.index] = value;
            default -> throw new IllegalArgumentException("Unexpected dst operand: " + dst + "Src value: " + value);
        }
    }
}
