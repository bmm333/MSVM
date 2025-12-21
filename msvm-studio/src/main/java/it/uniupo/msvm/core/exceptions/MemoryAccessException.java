package it.uniupo.msvm.core.exceptions;

public class MemoryAccessException extends  VmException{
    private final int address;

    public MemoryAccessException(int address, String message) {
        //Formatto il messagio in modo da includere anche lAddress in hex
        super(String.format("%s [Address: 0x%04X]",message,address));
        this.address=address;
    }
    public int getAddress(){return address;}
}
