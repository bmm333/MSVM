/**
 * Enumerazione che definisce il set di istruzioni (ISA) della macchina virtuale.
 * <p>
 * Ogni Opcode è associato a un byte univoco e al numero di argomenti che richiede.
 * Questo enum funge da tabella di decodifica principale per la CPU.
 * </p>
 */
package it.uniupo.msvm.core.instructions;

public enum Opcode {
    //istruzioni stack
    PUSH(0x01,1),
    POP(0x02),
    DUP(0x10),
    SWAP(0x11),
    //istruzioni aritmetiche
    ADD(0x03),
    SUB(0x04),
    MUL(0x05),
    DIV(0x06),

    //istruzioni salto
    JUMP(0x07,1),
    JZ(0x08,1),

    //istruzioni logiche
    AND(0x09),
    OR(0x0a),
    NOT(0x0b),
    SHL(0x0c),
    SHR(0x0d),

    //istruzioni IF
    IFGT(0x0e,1),
    IFLT(0x0f,1),

    //istruzione stop
    HALT(0xFF);

    private final int code;
    private final int argCount;
    Opcode(int code,int argCount) {
        this.code = code;
        this.argCount = argCount;
    }
    Opcode(int code) {
        this(code,0);
    }
    public static Opcode fromByte(int code) {
        for (Opcode op : values()) {
            if (op.code == code) return op;
        }
        throw new IllegalArgumentException("Unknown Opcode: " + code);
    }
    //Manual getters invece di lombok che causa errori
    public int getCode() {
        return code;
    }

    public int getArgCount() {
        return argCount;
    }
}