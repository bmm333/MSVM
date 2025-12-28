/*
* Sara da redifinire giustamente
* @lucalupi gli sto creando ora per fare i test della CPU
*
* */


package it.uniupo.msvm.core.instructions;

public enum Opcode {
    PUSH(0x01),
    ADD(0x02),
    HALT(0xFF),
    JUMP(0x09),
    JZ(0x10);

    private final int code;

    Opcode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Opcode fromByte(int code) {
        for (Opcode op : values()) {
            if (op.code == code) return op;
        }
        throw new IllegalArgumentException("Unknown Opcode: " + code);
    }
}