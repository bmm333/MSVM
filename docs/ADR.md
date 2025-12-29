ADR 001: Core Architecture & Runtime Model
Data: 28/12/2025 Status:Accepted Author: Arben Mema Context:MSVM

1. Context & Problem Statement
Stiamo Sviluppando una Virtual Machine a stack. La sfida principale non e solo l'esecuzione
delle istruzioni, ma la gestione della complessita derivante da:
    1.Concurrency: L'esecuzione della VM non deve bloccare il thread della UI.
    2. Coupling: Le istruzioni non devono dipendere dall'implementazione interna della CPU.
    3. Error Handling: Dobbiamo distinguere tra errori del 'programma utente' e crash sistema.
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
2.3 Concurrency : VmRunner
Per gestire lesecuzione, evitiamo di esporrere la gestione dei Thread alla UI(che crea Shallow Modules e Leakage of Complexity)
Abbiamo Creato VmRunner:
   -Interface:start(),pause(),setFrequency(Hz)
   -Implementation: Gestisce un Worker Thread dedicato. Usa il pattern Monitor per gestire la pausa in modo efficente.Gestisce il Throttling temporale per simulare la velocita di clock.
   -Data: Usa VmStateSnapshot (record immutabile) per passare i dati alla UI, eliminando alla radice le Race Conditons.
3. Comments
Nel codice i commenti seguono la regola: "Commenta il perche non che cosa", quindi spiegare l'astrazione e le decisioni di design non visibili.

4. Consequences
   -Manutenbilita: Possiamo cambiare limplementazione intera della CPU o del Threading senza rompere le istruzioni o la UI.
   -Safety: Il sistema e thread safe by design grazie agli Snapshot e all'incapsulamento del Runner.

To Add : technical debt
