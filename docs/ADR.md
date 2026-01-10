ADR 001: Core Architecture & Runtime Model
Data: 28/12/2025 Status:Accepted Author: Arben Mema Context:MSVM

1. Context & Problem Statement
Stiamo Sviluppando una Virtual Machine a stack. La sfida principale non e solo l'esecuzione
delle istruzioni, ma la gestione della complessita derivante da:
    1.Concurrency: L'esecuzione della VM non deve bloccare il thread della UI.
    2. Coupling: Le istruzioni non devono dipendere dall'implementazione interna della CPU.
    3. Error Handling: Dobbiamo distinguere tra errori del 'programma utente' e crash sistema.
    4. Dependency Management: Necessità di importare librerie esterne sia da file system locale che da repository remoti.
Imposto come obbiettivo quello di applicare la filosofia dei Deep Modules: esporre interface semplici nascondendo una logica robusta e compelssa
   
2. Architectural Decision
2.1 Execution Context(Pattern: Facade/Command)
Invece di passare lintera instanza Cpu alle singole istruzioni(che esporrebbe metodi pericolosi e High Coupling),
introduciamo ExecutionContext.
   -Design Choice: Le instruzioni implementatno linterfaccia funzionale Instruction che acceta solo ExecutionContext.
   -Why: Questo riduce il load pe r chi sta implementando le istruzioni vedendo solo cio che gli serve (push pop fetchnextbyte) e non puo rompere
        lo stato interno della CPU. Interfaccia stretta per una funzionalita profonda.
2.2 Exception Hierarchy
Abbiamo creato una gerarchi specifica radicata in VmException.
   -VmException: Base per errori generici della macchina.
   -MemoryAccessException: Per violazioni di accesso (SegFault).
   -OpcodeException: Per istruzioni illegali o non implementate.
    Why: Permette al runtime di catturare e gestire gli errori in modo granulare con messagi di errore precisi al utente senza fare craashare lintero sistema.
2.3 Concurrency & Thread safety (VmRunner)
Per gestire lesecuzione, evitiamo di esporrere la gestione dei Thread alla UI(che crea Shallow Modules e Leakage of Complexity)
Abbiamo Creato VmRunner:
   -Interface:start(),pause(),setFrequency(Hz)
   -Implementation: Gestisce un Worker Thread dedicato. Usa il pattern Monitor per gestire la pausa in modo efficente.Gestisce il Throttling temporale per simulare la velocita di clock.
   -Data: Usa VmStateSnapshot (record immutabile) per passare i dati alla UI, eliminando alla radice le Race Conditons.
2.4 Assembler Strategy (Two-Pass Algorithm)
   Per la traduzione del codice Assembly in Bytecode, abbiamo scartato l'approccio "One-Pass" (lettura sequenziale singola) a favore di un algoritmo Two-Pass.
    -Pass 1 (Symbol Resolution): Scansione del codice per mappare etichette (Label -> Address) nella Symbol Table.
    -Pass 2 (Code Generation): Traduzione effettiva, risolvendo i salti (JUMP/JZ) utilizzando gli indirizzi assoluti calcolati nel Pass 1.
    Why: Permette all'utente di definire salti in avanti (Forward References) senza dover calcolare manualmente gli offset di memoria.

2.5 Hybrid Import Resolution(Pattern: Composite/Cache-Aside)
Per gestire la direttiva @import, abbiamo implementato una strategia di risoluzione ibrida(HybridImportResolver)
        -Layer 1(Locale): verifica la presenza della libreria nella cache locale(./libs)
        -Layer 2 (Remote - RMI): Se assente, interroga il repository remoto via RMI.
        -Persistence: Le librerie scaricate vengono salvate localmente per accessi futuri (Cache-Aside).
        -Why: Garantisce operatività offline, riduce la latenza di rete per import ripetuti e disaccoppia la logica di assemblaggio dalla sorgente del codice.
3. Comments Philosophy
Nel codice i commenti seguono la regola: "Commenta il perche non che cosa", quindi spiegare l'astrazione e le decisioni di design non visibili.

4. Consequences
   -Manutenbilita: Possiamo cambiare limplementazione intera della CPU o del Threading senza rompere le istruzioni o la UI.
   -Safety: Il sistema e thread safe by design grazie agli Snapshot e all'incapsulamento del Runner.

5. Debito Tecnico:
    -Debito Tecnico (Limitazioni Accettate) Per rispettare le scadenze del progetto, abbiamo accettato consapevolmente le seguenti limitazioni:
    -Source Mapping Assente: L'Assembler rimuove commenti e spazi bianchi. In caso di errore a runtime, l'Instruction Pointer (IP) punta all'indirizzo di memoria, ma non siamo ancora in grado di evidenziare la riga corrispondente nel codice sorgente originale.
    -Loop Infiniti: La CPU non ha un meccanismo di sicurezza (watchdog) per interrompere loop infiniti nel codice Assembly. L'utente deve interrompere l'esecuzione manualmente tramite il pulsante Stop.
    -Dimensione Memoria: La memoria ha una dimensione fissa definita all'avvio. Non è supportata l'allocazione dinamica o la paginazione.