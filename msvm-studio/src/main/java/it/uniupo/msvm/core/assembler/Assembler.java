package it.uniupo.msvm.core.assembler;

import it.uniupo.msvm.core.exceptions.VmException;
import it.uniupo.msvm.core.instructions.Opcode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsabile della traduzione del codice Assembly in bytecode macchina.
 * Implementa un algoritmo a due passaggi (Two-Pass) per supportare la risoluzione
 * delle etichette simboliche (Label).
 *
 * @author Arben Mema
 */
public class Assembler {
    /**
     * Tabella dei simboli per memorizzare le associazioni tra Etichette (Label) e Indirizzi di memoria.
     * La chiave è il nome della label (es. "LOOP"), il valore è l'indice nell'array del bytecode.
     */
    private final Map<String, Integer> symbolTable = new HashMap<>();
    /**
     * Trasforma un programma Assembly in un array di int.
     * Esegue l'assemblaggio in due fasi:
     * <ol>
     * <li>Scansione per identificare le etichette e calcolare gli indirizzi.</li>
     * <li>Generazione effettiva del bytecode e risoluzione dei salti.</li>
     * </ol>
     *
     * @param source il codice sorgente completo.
     * @return L'array di bytecode pronto per la VM.
     * @throws VmException se vengono rilevati errori di sintassi o label non definite.
     */
    public int[] assemble(String source) {
        if (source == null || source.isBlank()) {
            return new int[0];
        }

        // Pulizia e normalizzazione delle righe
        String[] lines = source.split("\n");
        // Pulisce la tabella dei simboli prima di iniziare
        symbolTable.clear();

        try {
            //PRIMO PASSAGGIO: Mappatura Label
            firstPass(lines);
            //SECONDO PASSAGGIO: Generazione Bytecode
            return secondPass(lines);
        } catch (Exception e) {
            throw new VmException("Errore durante l'assemblaggio: " + e.getMessage());
        }
    }

    /**
     * Esegue il primo passaggio (First Pass).
     * Scopo: Popolare la {@link #symbolTable} con gli indirizzi delle etichette.
     * <p>
     * Logica: Simula la generazione del codice incrementando un contatore (program counter)
     * per ogni istruzione e argomento, senza scrivere nulla. Quando trova una label (es. "LOOP:"),
     * salva la posizione corrente nella mappa.
     * V1.1 del first pass include anche lindirizzo della riga per errori per un migliore DX (dev experience),
     * popolando  la {@link #symbolTable} con indirizzi delle etichette.
     * </p>
     *
     * @param lines le righe del codice sorgente.
     */
    private void firstPass(String[] lines) {
        int currentAddress = 0;
        //ciclo for-i per sapere il numero di riga
        for (int i = 0; i < lines.length; i++) {
            int lineNumber = i + 1; // Teniamo traccia della riga per i log di errore
            String line = cleanLine(lines[i]);
            if (line.isEmpty()) continue;
            if (isLabelDefinition(line)) {
                // Esempio: "LOOP:" -> "LOOP"
                // Normalizziamo per evitare case-sensitivity issues
                String labelName = line.substring(0, line.length() - 1).trim().toUpperCase();
                // Controllo per duplicati
                if (symbolTable.containsKey(labelName)) {
                    throw new VmException("Error at line " + lineNumber + ": Duplicate label '" + labelName + "'");
                }
                symbolTable.put(labelName, currentAddress);
            } else {
                // Parse instruction per contare i byte
                String[] parts = line.split("\\s+");
                if (parts.length == 0) continue;
                String mnemonic = parts[0].toUpperCase();

                try {
                    Opcode opcode = Opcode.valueOf(mnemonic);
                    currentAddress += 1; // byte per opcode
                    if (opcode.getArgCount() > 0) {
                        currentAddress += 1; // byte per argomento
                    }
                } catch (IllegalArgumentException e) {
                    throw new VmException("Error at line " + lineNumber + ": Unknown instruction '" + mnemonic + "'");
                }
            }
        }
    }

    /**
     * Esegue il secondo passaggio (Second Pass).
     * Scopo: Tradurre le istruzioni in interi e risolvere i simboli usando la tabella.
     *
     * @param lines le righe del codice sorgente.
     * @return l'array di interi del bytecode.
     */
    private int[] secondPass(String[] lines) {
        List<Integer> bytecode = new ArrayList<>();

        for (int i = 0; i < lines.length; i++) {
            String line = cleanLine(lines[i]);
            if (line.isEmpty() || isLabelDefinition(line)) {
                continue; // Le definizioni di label non generano codice nel secondo pass
            }
            parseInstruction(line, bytecode, i + 1);
        }
        return bytecode.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Parsifica una singola riga di istruzione e aggiunge i relativi opcode e argomenti al bytecode.
     *
     * @param line la riga da parsare.
     * @param bytecode la lista dove aggiungere il bytecode.
     * @param lineNumber il numero di riga per errori.
     */
    private void parseInstruction(String line, List<Integer> bytecode, int lineNumber) {
        String[] parts = line.split("\\s+");
        if (parts.length == 0) return;
        String mnemonic = parts[0].toUpperCase();

        Opcode opcode;
        try {
            opcode = Opcode.valueOf(mnemonic);
        } catch (IllegalArgumentException e) {
            throw new VmException("Syntax Error at line " + lineNumber + ": Unknown instruction '" + mnemonic + "'");
        }
        bytecode.add(opcode.getCode());
        if (opcode.getArgCount() > 0) {
            if (parts.length < 2) {
                throw new VmException("Syntax Error at line " + lineNumber + ": Missing argument for '" + mnemonic + "'");
            }
            String argStr = parts[1];
            int arg;
            try {
                arg = Integer.decode(argStr);
            } catch (NumberFormatException e) {
                // Check if label
                String labelKey=argStr.toUpperCase(); //normalizzazione
                if (symbolTable.containsKey(labelKey)) {
                    arg = symbolTable.get(labelKey);
                } else {
                    throw new VmException("Syntax Error at line " + lineNumber + ": Undefined label '" + argStr + "'");
                }
            }
            bytecode.add(arg);
        }
    }

    /**
     * Rimuove commenti e spazi superflui da una riga.
     * * @param line la riga grezza.
     * @return la riga pulita.
     */
    private String cleanLine(String line) {
        if (line.contains(";")) {
            line = line.substring(0, line.indexOf(";"));
        }
        return line.trim();
    }

    /**
     * Verifica se una riga è la definizione di una label.
     * * @param line la riga da analizzare.
     * @return true se la riga termina con ':', false altrimenti.
     */
    private boolean isLabelDefinition(String line) {
        return line.endsWith(":");
    }
}