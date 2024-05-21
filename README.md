# Dilema Prizonierului

Acest proiect simulează Dilema Prizonierului într-un sistem multi-agent, utilizând platforma JADE pentru a gestiona comportamentul agenților și include o interfață grafică pentru vizualizarea și interacțiunea cu simularea.

## Descriere

Dilema Prizonierului este un scenariu clasic in teoria jocurilor care ilustreaza cum doi jucatori pot alege intre cooperare si tradare, cu diverse rezultate bazate pe alegerea combinata. Proiectul foloseste agenti software pentru a modela deciziile prizonierilor si un agent arbitru pentru a evalua rezultatele.

## Structura Proiectului

Proiectul include urmatoarele componente principale:

- `Main.java`: Punctul de intrare al aplicatiei, care initiaza simularea.
- `Prisoner.java`: Acest fisier defineste logica unui agent prizonier.
- `ArbitratorAgent.java`: Agentul care arbitreaza jocul si calculeaza scorurile.
- `ArbitratorGUI.java`: Clasa care gestionează interfața grafică, oferind vizualizări și controale pentru simulare.
  
## Cerinte de Sistem

Pentru a rula acest proiect, ai nevoie de:

- Java SE 17
- JADE 4.6.0

## Cum sa Rulezi Proiectul

### Instalare

1. Instaleaza Java Development Kit (JDK) pentru Java 17 de la [Oracle JDK](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) sau orice distributie compatibila.
2. Descarca si instaleaza JADE 4.6.0 de la [JADE's website](http://jade.tilab.com/download/jade/license/jade-download/).

### Configurarea si Rularea Aplicatiei

Pentru a rula simularea, trebuie sa configurezi agentii prizonieri cu strategii specifice si sa setezi numarul de runde. Iata un exemplu de cod pentru configurarea si pornirea agentilor:

```java
Object[] arbitratorArgs = {"50"}; // Numarul de runde
AgentController arbitrator = mainContainer.createNewAgent("arbitrator", "ArbitratorAgent", arbitratorArgs);
arbitrator.start();

String[] prisoner1Args = {"AlwaysCooperate"}; // Strategia pentru primul prizonier
String[] prisoner2Args = {"AlwaysDefect"}; // Strategia pentru al doilea prizonier
AgentController prisoner1 = mainContainer.createNewAgent("prisoner1", "Prisoner", prisoner1Args);
AgentController prisoner2 = mainContainer.createNewAgent("prisoner2", "Prisoner", prisoner2Args);
prisoner1.start();
prisoner2.start();

// Inițializare și lansare GUI
GUI gui = new GUI();
gui.setVisible(true);
```

## Strategii Disponibile
Prizonierii pot folosi urmatoarele strategii in cadrul simularii:

- AlwaysCooperate: Prizonierul va coopera intotdeauna.
- AlwaysDefect: Prizonierul va trada intotdeauna.
- TitForTat: Prizonierul incepe cooperand si apoi replica ultima miscare a adversarului.
- Random: Prizonierul alege aleator intre cooperare si tradare.
- Grudger: Prizonierul coopereaza pana cand adversarul tradeaza prima data, apoi va trada mereu.
- Pavlov: Prizonierul schimba strategia doar daca runda precedenta a fost pierzatoare.
