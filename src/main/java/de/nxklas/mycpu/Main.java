package de.nxklas.mycpu;

import de.nxklas.mycpu.core.Processor;

/*
    IMMEDIATE(0b00),
    REGISTER(0b01);

    NOP(0x00),
    MOV(0x10),
    HALT(0xFF);
*/
public class Main {
    private static final byte[] program = {
        0x10, 0x04, 0x00, 0x03
    };

    public static void main(String[] args) {
        var processor = new Processor(program);
        processor.execute();
        System.out.println("Hello, world");
    }
}
