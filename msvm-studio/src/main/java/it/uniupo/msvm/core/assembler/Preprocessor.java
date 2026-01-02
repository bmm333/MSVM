package it.uniupo.msvm.core.assembler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Preprocessor {
    //Regex per catturare: @import bmm333/prog1
    // \s* gestisce spazi opzionali prima e dopo
    private static final Pattern IMPORT_PATTERN= Pattern.compile("^\\s*@import\\s+([\\w-]+/[\\w-]+)\\s*$");
    private final ImportResolver resolver;
    private final int MAX_DEPTH=10; // protezione contro StackOverflow da import
    public Preprocessor(ImportResolver resolver) {
        this.resolver = resolver;
    }
    public List<String> process(List<String> sourceLines)
    {
        return processRecursive(sourceLines,0);
    }
    private List<String> processRecursive(List<String> lines,int depth)
    {
        if(depth>MAX_DEPTH){ throw new RuntimeException("Import depth limit exceeded! Possible circular dependency.");}
        List<String>expandedCode= new ArrayList<>();
        for(String line:lines){
            Matcher matcher=IMPORT_PATTERN.matcher(line);
            if(matcher.matches()){
                String targetLib=matcher.group(1);
                try{
                    //Risolvo (Scarico) il codice
                    List<String> importedLines =resolver.resolve(targetLib);
                    //Aggiungo commenti di debug (UX)
                    expandedCode.add("; --- BEGIN IMPORT " + targetLib + " ---");
                    //Ricursione Processo anche le righe importate
                    expandedCode.addAll(processRecursive(importedLines,depth+1));
                    expandedCode.add("; --- END IMPORT " + targetLib + " ---");
                }catch (Exception e)
                {
                    throw new RuntimeException("Preprocessor Error on '" + targetLib + "': " + e.getMessage());
                }
            }else {
                //Riga normale la mantengo
                expandedCode.add(line);
            }
        }
        return expandedCode;
    }
}
