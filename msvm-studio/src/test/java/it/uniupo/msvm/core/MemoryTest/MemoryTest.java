package it.uniupo.msvm.core.MemoryTest;
import it.uniupo.msvm.core.exceptions.MemoryAccessException;
import it.uniupo.msvm.core.memory.*;
import org.junit.jupiter.api.*;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;


public class MemoryTest {
    private static Memory testMemory;
    @BeforeEach
    public void setUpBeforeClass() throws Exception {
        testMemory=new Memory(512);
        assertTrue(testMemory != null,"Memoria non creata\n");
    }

    @Test
    public void testWriteAndReadMemory() throws Exception {
        testMemory.write(0,10);
        assertEquals(10,testMemory.read(0),"Scrittura non riuscita");
        testMemory.write(1,20);
        assertEquals(20,testMemory.read(1),"Scrittura non riuscita");
        testMemory.write(1,30);
        assertEquals(30,testMemory.read(1),"Scrittura non riuscita");
    }

    @Test
    public void testExeptionReadTest() throws Exception {
        testMemory.write(0,10);
        assertThrows(MemoryAccessException.class,()->testMemory.read(-1),"Lettura valida");
        assertThrows(MemoryAccessException.class,()->testMemory.read(512),"Lettura valida");
        assertThrows(MemoryAccessException.class,()->testMemory.read(1024),"Lettura valida");
    }

    @Test
    public void testExeptionWriteTest() throws Exception {
        assertThrows(MemoryAccessException.class,()->testMemory.write(-1,10),"Scrittura valida");
        assertThrows(MemoryAccessException.class,()->testMemory.write(512,10),"Scrittura valida");
        assertThrows(MemoryAccessException.class,()->testMemory.write(1024,10),"Scrittura valida");
    }
    @Test
    public void testGetSize(){
        assertEquals(512,testMemory.sizeMemory(),"La memoria non è settata bene");
    }

    @Test
    public void testClearMemory() throws MemoryAccessException {
        for(int i=0;i<512;i++){
            testMemory.write(i,i+10);
        }
        for(int i=0;i<512;i++){
            assertEquals(i+10,testMemory.read(i),"Scrittura non riuscita");
        }
        testMemory.clear();
        for(int i=0;i<512;i++){
            assertEquals(0,testMemory.read(i),"Cancellazione non riuscita");
        }
    }

    @Test
    public void testLoadProgram() throws MemoryAccessException {
        int []program= new int[]{1,2,3,4};
        testMemory.loadProgram(program);
        for(int i=0;i<program.length;i++){
            assertEquals(i+1,testMemory.read(i));
        }
    }

    @Test
    public void testLoadProgramExption() throws MemoryAccessException {
        int []program=new int [1024];
        Arrays.fill(program,1);
        assertThrows(MemoryAccessException.class,()->testMemory.loadProgram(program),"Il programma va bene");
    }

}
