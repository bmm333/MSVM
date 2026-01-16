package it.uniupo.msvm.core.assembler;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PreProcessorTest {
    @Test
    void testSimpleImport(){
        //Creo resolver finto (Stub) che simula una libreria esistente
        ImportResolver mockResolver = target -> {
            if (target.equals("math/utils")) {
                return List.of("PUSH 10", "ADD");
            }
            throw new IllegalArgumentException("Not found");
        };
        Preprocessor preprocessor = new Preprocessor(mockResolver);
        //input: codice utente che usa limport
        List<String> source = List.of(
                "PUSH 5",
                "@import math/utils",
                "HALT"
        );
        //execute
        List<String>result=preprocessor.process(source);
        //Verify
        //Mi aspetto: Push 5 -> Commento -> Push 10 -> Add -> Commento -> Halt
        //Totale righe stimate 1 push + 1 commento + 2 lib + 1 commento + 1 halt = 6 righe
        assertEquals(6,result.size());
        assertTrue(result.contains("PUSH 10"));
        assertTrue(result.contains("; --- INIZIO IMPORT math/utils ---"));
    }
    @Test
    void testRecursiveImport()
    {
        // Testiamo A-> import B -> importa C
        ImportResolver mockResolver = target -> {
            switch (target) {
                case "lib/A": return List.of("@import lib/B");
                case "lib/B": return List.of("PUSH 99"); // Foglia
                default: throw new IllegalArgumentException("Not found");
            }
        };
        Preprocessor preprocessor = new Preprocessor(mockResolver);
        List<String> source = preprocessor.process(List.of("@import lib/A"));
        List<String> result = preprocessor.process(source);
        //Deve aver risolto tutto fino a trovare PUSH 99
        assertTrue(result.stream().anyMatch(s -> s.contains("PUSH 99")));
    }
    @Test
    void testCircularDependencyProtection() {
        ImportResolver mockResolver = target -> List.of("@import lib/A"); // Loop!
        Preprocessor preprocessor = new Preprocessor(mockResolver);
        List<String> source = List.of("@import lib/A");
        assertThrows(RuntimeException.class,()->{preprocessor.process(source);});
    }
}
