package it.uniupo.msvm.core.assembler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Il Preprocessor gestisce le direttive speciali nel codice sorgente prima dell'assemblaggio.
 * Attualmente supporta la direttiva {@code @import} per includere librerie esterne.
 */
public class Preprocessor {
    /** Pattern Regex per catturare la direttiva: @import nome_utente/nome_libreria */
    private static final Pattern IMPORT_PATTERN = Pattern.compile("^\\s*@import\\s+([\\w-]+/[\\w-]+)\\s*$");
    /** Risolutore per caricare il contenuto delle librerie importate. */
    private final ImportResolver resolver;
    /** Profondità massima di importazione per evitare ricorsioni infinite. */
    private final int MAX_DEPTH = 10;

    /**
     * Costruttore con risolutore di importazioni.
     *
     * @param resolver il risolutore da utilizzare.
     */
    public Preprocessor(ImportResolver resolver) {
        this.resolver = resolver;
    }

    /**
     * Elabora le righe del codice sorgente espandendo le direttive.
     *
     * @param sourceLines le righe di codice originale.
     * @return una lista di righe con gli import espansi.
     */
    public List<String> process(List<String> sourceLines) {
        return processRecursive(sourceLines, 0);
    }

    /**
     * Metodo ricorsivo per elaborare gli import.
     *
     * @param lines righe da elaborare.
     * @param depth profondità attuale della ricorsione.
     * @return lista di righe elaborate.
     * @throws RuntimeException se viene superato il limite di profondità o in caso di errore di risoluzione.
     */
    private List<String> processRecursive(List<String> lines, int depth) {
        if (depth > MAX_DEPTH) {
            throw new RuntimeException("Limite di profondità import superato! Possibile dipendenza circolare.");
        }
        List<String> expandedCode = new ArrayList<>();
        for (String line : lines) {
            Matcher matcher = IMPORT_PATTERN.matcher(line);
            if (matcher.matches()) {
                String targetLib = matcher.group(1);
                try {
                    // Risolvo (Scarico) il codice
                    List<String> importedLines = resolver.resolve(targetLib);
                    // Aggiungo commenti di debug (UX)
                    expandedCode.add("; --- INIZIO IMPORT " + targetLib + " ---");
                    // Ricorsione: processo anche le righe importate
                    expandedCode.addAll(processRecursive(importedLines, depth + 1));
                    expandedCode.add("; --- FINE IMPORT " + targetLib + " ---");
                } catch (Exception e) {
                    throw new RuntimeException("Errore del preprocessore su '" + targetLib + "': " + e.getMessage());
                }
            } else {
                // Riga normale: la mantengo
                expandedCode.add(line);
            }
        }
        return expandedCode;
    }
}
