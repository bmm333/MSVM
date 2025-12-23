package it.uniupo.msvm.core.exceptions;

public class OpcodeException extends VmException {
    private final int illegalByte;

    public OpcodeException(int illegalByte) {
        super(String.format("Unknown Opcode: 0x%02X", illegalByte));
        this.illegalByte = illegalByte;
    }

    public int getIllegalByte() {
        return illegalByte;
    }
}