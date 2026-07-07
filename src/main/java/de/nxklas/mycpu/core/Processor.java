package de.nxklas.mycpu.core;

import de.nxklas.mycpu.core.instructions.*;
import de.nxklas.mycpu.core.operands.*;

public class Processor {
    private final Instruction[] program;
    private final int[] registers;
    private final Alu alu;
    private boolean isRunning;
    private int pc;

    public Processor(Instruction[] program) {
        this.program = program;
        this.registers = new int[10];
        this.alu = new Alu();
        this.isRunning = false;
        this.pc = 0;
    }

    // Only for testing purposes.
    Processor(Instruction[] program, int pc) {
        this(program);
        this.pc = pc;
    }

    int peekRegister(int index) {
        if (index < 0 || index >= registers.length)
            throw new IllegalArgumentException(
                    "Argument 'index' must be in range of 0 to " + (registers.length - 1) + ". Actual value: " + index);
        return registers[index];
    }

    void writeRegister(int index, int value) {
        if (index < 0 || index >= registers.length)
            throw new IllegalArgumentException(
                    "Argument 'index' must be in range of 0 to " + (registers.length - 1) + ". Actual value: " + index);
        registers[index] = value;
    }

    int peekFlags() {
        return alu.flags();
    }

    int peekProgramCounter() {
        return pc;
    }

    public void execute() {
        isRunning = true;
        while (isRunning && pc < program.length) {
            var instrcution = next();
            execute(instrcution);
        }

        if (pc > program.length)
            throw new IllegalArgumentException("Program has never ended properely.");
    }

    private void execute(Instruction instruction) {
        switch (instruction) {
            case NopInstruction _ -> nop();
            case MovInstruction movInstruction -> mov(movInstruction);
            case AddInstruction addInstruction -> add(addInstruction);
            case SubInstruction subInstruction -> sub(subInstruction);
            case CmpInstruction cmpInstruction -> cmp(cmpInstruction);
            case JmpInstruction jmpInstruction -> jmp(jmpInstruction);
            case HaltInstruction _ -> halt();
        }
    }

    private void nop() {
    }

    private void mov(MovInstruction instruction) {
        var srcValue = resolve(instruction.src());
        write(instruction.dst(), srcValue);
    }

    private void add(AddInstruction instruction) {
        var dstValue = resolve(instruction.dst());
        var srcValue = resolve(instruction.src());
        var newValue = alu.add(dstValue, srcValue);
        write(instruction.dst(), newValue);
    }

    private void sub(SubInstruction instruction) {
        var dstValue = resolve(instruction.dst());
        var srcValue = resolve(instruction.src());
        var newValue = alu.sub(dstValue, srcValue);
        write(instruction.dst(), newValue);
    }

    private void cmp(CmpInstruction instruction) {
        var dstValue = resolve(instruction.dst());
        var srcValue = resolve(instruction.src());
        var _ = alu.sub(dstValue, srcValue);
    }

    private void jmp(JmpInstruction instruction) {
        var dstValue = resolve(instruction.dst());

        switch (instruction.kind()) {
            case None:
                jmp(dstValue);
                break;
            case Equals:
            case NotEquals:
            case Less:
            case LessOrEquals:
            case Greater:
            case GreaterOrEquals:
                var condition = (instruction.kind().value & alu.flags()) != 0;
                if (condition)
                    jmp(dstValue);
                break;
            default:
                throw new IllegalArgumentException("Unexpected jmp kind: " + instruction.kind());
        }
    }

    private void jmp(int dst) {
        if (dst < 0 || dst >= program.length)
            throw new IllegalArgumentException("Cannot jump to " + dst);
        pc = dst;
    }

    private void halt() {
        isRunning = false;
    }

    private Instruction next() {
        return program[pc++];
    }

    int resolve(Operand op) {
        return switch (op) {
            case ImmediateOperand i -> i.value();
            case RegisterOperand i -> registers[i.index()];
            default -> throw new IllegalArgumentException("Unexpected operand to resolve: " + op);
        };
    }

    void write(Operand dst, int value) {
        switch (dst) {
            case ImmediateOperand _ -> throw new IllegalArgumentException(
                    "Cannot write to immediate, since writing to is only permitted to registers. Dst operand: "
                            + dst + "Src value: " + value);
            case RegisterOperand register -> registers[register.index()] = value;
            default -> throw new IllegalArgumentException("Unexpected dst operand: " + dst + "Src value: " + value);
        }
    }
}
