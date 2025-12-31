package it.uniupo.msvm.core.assembler;

import it.uniupo.msvm.core.exceptions.VmException;
import it.uniupo.msvm.core.instructions.Opcode;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Resposabile della traduzione del codice Assembly leggibile dall'uomo in bytecode macchina.
 * Questo  componente agisce come un Deep Module: astrae la complessita di analisi lessicale, risoluzione delle ettichete e mappatura degli opcode
 * dietro una singola chiamata di metodo.
 * */
public class Assembler {
    /**
     * Trasforma un programma Assembly in un array di int.
     *
     * @param source il codice sorgente completo come singola stringa.
     * @return L'array di bytecode pronto per essere caricato nella memoria della VM.
     * @throws it.uniupo.msvm.core.exceptions.VmException se vengono rilevati errori di sintatssi
     *
     */
    public int[] assemble(String source) {
        if (source == null || source.isBlank()) {
            return new int[0];
        }
        List<Integer> bytecode = new ArrayList<>();
        //Normalizzazione: Divisione in righe per mantenere il riferimento
        //ai numeri di riga in caso di errore.
        String[] lines = source.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            int lineNumber = i + 1;
            // Design Decision: Ignoriamo righe vuote e commenti (che iniziano con ';')
            // per permettere una formattazione leggibile del codice sorgente.
            if (line.isEmpty() || line.startsWith(";")) {
                continue;
            }
            //Rimuove eventuali commenti inline (Push 10; Commento)
            //per isolare l'istruzione pura
            if(line.contains(";"))
            {
                line=line.substring(0,line.indexOf(";")).trim();
            }
            try{
                parseLine(line,bytecode);
            }catch (Exception e)
            {
                //Wrap del errore per fornire contesto all utente
                throw new VmException("Syntax Error at line " + lineNumber + ": "+ e.getMessage());
            }
        }
        //Conversione da List<Integer> a int[] primitvo per performance e compatibilita
        return bytecode.stream().mapToInt(i->i).toArray();
    }
    /**
     * Parsifica una singola riga di istruzione e aggiunge i relativi opcode al buffer
     * */
    private void parseLine(String line,List<Integer>bytecode)
    {
        //Tokenizzazione basata sugli spazi bianchi multi
        String[] parts=line.split("\\s+");
        String mnemonic=parts[0].toUpperCase();

        Opcode opcode;
        try{
            opcode= Opcode.valueOf(mnemonic);
        }catch (IllegalArgumentException e)
        {
            throw new IllegalArgumentException("Unknown instruction '" + mnemonic + "'");
        }
        bytecode.add(opcode.getCode());
        if(opcode.getArgCount()>0)
        {
            if(parts.length<2)
            {
                throw new IllegalArgumentException("Missing argument for '" + mnemonic + "'");
            }
            try{
                int arg=Integer.decode(parts[1]);
                bytecode.add(arg);
            }catch (NumberFormatException e)
            {
                throw new IllegalArgumentException("Invalid number format: "+ parts[1]);
            }
        }
    }
}
