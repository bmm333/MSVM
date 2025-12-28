/*
* Sara da redifinire giustamente
* @lucalupi gli sto creando ora per fare i test della CPU
*
* */


package it.uniupo.msvm.core.instructions;

import lombok.Getter;

@Getter
public enum Opcode {
    //istruzioni stack
    PUSH(0x01),
    POP(0x02),

    //istruzioni aritmentiche
    ADD(0x03),
    SUB(0x04),
    MUL(0x05),
    DIV(0x06),

    //istruzioni salto
    JUMP(0x07),
    JZ(0x08),

    //istruzioni logiche
    AND(0x09),
    OR(0x0a),
    NOT(0x0b),
    SHL(0x0c),
    SHR(0x0d),

    //istruzione stop
    HALT(0xFF);

    private final int code;

    Opcode(int code) {
        this.code = code;
    }

    public static Opcode fromByte(int code) {
        for (Opcode op : values()) {
            if (op.code == code) return op;
        }
        throw new IllegalArgumentException("Unknown Opcode: " + code);
    }
}