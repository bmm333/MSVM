package it.uniupo.msvm.core.assembler;

import java.util.List;

public interface ImportResolver {
    /**
     * Cerca il codice sorgente per il progetto data.
     * @param dependencyIdentifier es "bmm333/prog1"
     * @return Le righe di codice Assembly della Libreria
     * @throws IllegalArgumentException se non trova la libreria
     * */
    List<String> resolve(String dependencyIdentifier);
}
