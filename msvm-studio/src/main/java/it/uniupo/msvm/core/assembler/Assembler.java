package it.uniupo.msvm.core.assembler;

import it.uniupo.msvm.core.exceptions.VmException;
import it.uniupo.msvm.core.instructions.Opcode;

import java.util.*;

/**
 * The Assembler class is responsible for assembling assembly-like language into bytecode.
 * It processes source code, parses its instructions, and outputs a corresponding
 * bytecode array. The assembler supports labels, instructions, and optional preprocessing.
 */
public class Assembler {
    /**
     * Tabella dei simboli per memorizzare le associazioni tra Etichette (Label) e Indirizzi di memoria.
     * La chiave è il nome della label (es. "LOOP"), il valore è l'indice nell'array del bytecode.
     */
    private final Map<String, Integer> symbolTable = new HashMap<>();

    private final Preprocessor preprocessor;

    public Assembler() {
        this(null);
    }

    public Assembler(Preprocessor preprocessor) {
        this.preprocessor = preprocessor;
    }


    /**
     * Entry point di convenienza per compilare codice sorgente fornito come stringa grezza.
     * <p>
     * Questo metodo funge da wrapper: normalizza l'input dividendolo in righe (basandosi sul carattere
     * di fine riga) e delega l'elaborazione al metodo principale {@link #assemble(List)}.
     * </p>
     * <p>
     * <b>Nota:</b> Anche utilizzando questo metodo, la pipeline completa (incluso il Preprocessing
     * e la risoluzione degli {@code @import}) viene eseguita regolarmente.
     * </p>
     *
     * @param source Il codice Assembly completo sotto forma di stringa unica.
     * @return L'array di bytecode generato. Restituisce un array vuoto se l'input è null o vuoto.
     * @throws VmException In caso di errori di sintassi, label duplicate o dipendenze mancanti
     * propagati dalla pipeline principale.
     * @see #assemble(List)
     */
    public int[] assemble(String source) {
        if (source == null || source.isBlank()) {
            return new int[0];
        }
        // Wrapper: converte in lista e chiama il metodo "vero"
        return assemble(Arrays.asList(source.split("\n")));
    }
    /**
     * Entry point  che accetta una lista di righe di codice.
     * <p>
     * Questo metodo orchestra l'intera pipeline di compilazione:
     * <ol>
     * <li><b>Preprocessing:</b> Se un preprocessor è configurato, espande gli import e processa le macro.</li>
     * <li><b>Assemblaggio:</b> Esegue la logica standard a due passaggi (Two-Pass) sui dati processati.</li>
     * </ol>
     * </p>
     *
     * @param lines Le righe del codice sorgente (potenzialmente contenenti direttive come {@code @import}).
     * @return L'array di bytecode compilato pronto per l'esecuzione.
     * @throws VmException se si verificano errori durante il preprocessing (es. libreria non trovata)
     * o durante l'assemblaggio (es. sintassi errata, label duplicate).
     */
    public int[] assemble(List<String>lines)
    {
        //prima fase Preprocessing
        if(preprocessor!=null)
        {
            try{
                lines=preprocessor.process(lines);
            }catch (Exception e){
                throw new VmException("Preprocessor Error: " + e.getMessage());
            }
        }
        //Seconda fase Assemblaggio (Core logic)
        //converto in array per mantenere compatibilita con la logica precedente
        String[] linesArray=lines.toArray(new String[0]);
        symbolTable.clear();

        try{
            //mappatura label (firstpass)
            firstPass(linesArray);
            //Generazione Bytecode(Secondpass)
            return secondPass(linesArray);
        }catch (Exception e){
            //Evitiamo di wrappare una vmexception dentro un altra
            if(e instanceof VmException) throw (VmException)e;
            throw new VmException("Assembler Error: " + e.getMessage());
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
     * popolando la {@link #symbolTable} con indirizzi delle etichette.
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