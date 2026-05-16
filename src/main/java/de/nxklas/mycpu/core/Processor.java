package de.nxklas.mycpu.core;

import de.nxklas.mycpu.core.instructions.*;
import de.nxklas.mycpu.core.operands.*;
import de.nxklas.mycpu.util.Tuple;

public class Processor {
    private final byte[] program;
    private final int[] registers;
    private final Alu alu;
    private boolean isRunning;
    private int pc;

    public Processor(byte[] program) {
        this.program = program;
        this.registers = new int[10];
        this.alu = new Alu();
        this.isRunning = false;
        this.pc = 0;
    }

    // Only for testing purposes.
    Processor(byte[] program, int pc) {
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

    Instruction decode(byte opcode) {
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
            case HALT:
                return new HaltInstruction();
            default:
                throw new IllegalArgumentException("Unexpected opcode in instruction decode: " + opcode);
        }
    }

    private Tuple<Operand, Operand> readDstSrcPair() {
        var mode = AccessMode.decode(next());
        var dst = readOperand(mode.value1);
        var src = readOperand(mode.value2);
        return new Tuple<Operand, Operand>(dst, src);
    }

    private void execute(Instruction instruction) {
        switch (instruction) {
            case NopInstruction _ -> nop();
            case MovInstruction movInstruction -> mov(movInstruction);
            case AddInstruction addInstruction -> add(addInstruction);
            case SubInstruction subInstruction -> sub(subInstruction);
            case CmpInstruction cmpInstruction -> cmp(cmpInstruction);
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
