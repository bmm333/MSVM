
ADR 001: Uttilizzo del Architettura Stack based per VM  
ADR 002: Istruzioni del CPU
### Stato Pending
### Contesto: La cpu del mini stack vm deve codificare ed eseguire diverse istr (OPcode).
###         |-> Le scelte che prese in considerazione erano 2: Switch-case Monolitico all'interno del ciclo di fetch-decode-execute.
###         |-> Piu veloce , stile C e ASM , pero violando OCP.
###         |-> Per cio pensando che altri svilupattori vogliano estendere il set di istruzioni , e meglio evitare una clasee 
###         |-> Cpu che ha migliaia di rige di codice e difficile da testare e mantenere
### Decisione: Penso di implementare ogni istr come una classe separata che implementa una interfaccia comune `Instrucion`
###         |-> La Cpu utilizzera una Map<OpCode,Instruction> per delegare lesecuzione (Pattern : Strategy)
### Conseguenze:
##### Pro:  SRP: Ogni classe istr fa una sola cosa.categories:
#####       OCP: E possibile aggiungere nuove istr senza ricompilare o modificare la 'CPU'.
#####       Testibilita: Ogni istr puo essere testata in isolamento 
##### Negative: Overhead: Uttilizo di risorese aumenta ( memoria: creando oggetti per ciascun istr) e un leggero overhead  del dispatch risp a Switch Case
#####           Complesita: Aumento del numero di classi nel package `core.instructions`
## Implementazione Tecnica
##  Si utilizzerà una classe astratta `BaseInstruction` per fattorizzare la logica comune (es. logging e toString).

s