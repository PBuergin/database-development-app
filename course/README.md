# Kurs-Checkpoints

Der normale Unterricht startet weiterhin im Root von `db-2-app`. Die Checkpoints sind Hilfszustaende fuer Quereinstieg, Nacharbeit und Loesungseinsicht.

## Zustaende

| Zustand | Bedeutung |
| --- | --- |
| `block-2-start` | Starter fuer Block 2 mit bewusst schwacher `V1` |
| `block-2-complete` | Block-2-Loesung mit `V2__enforce_ticket_rules.sql` |
| `block-3-start` | Einstieg in Block 3 auf Basis von `block-2-complete` |
| `block-3-complete` | Block-3-Loesung mit Transaktionsworkflow |

## Arbeitskopie erzeugen

```bash
./course-state create block-3-start ../work/db-2-app-block-3
```

Die Zielkopie ist ein normales Maven-Projekt. Sie darf bearbeitet, getestet und wieder geloescht werden.

## Checkpoints pruefen

Einen einzelnen Zustand pruefen:

```bash
./course-state test block-3-complete
```

Alle Zustaende pruefen:

```bash
./course-state validate
```

Fuer schnelle lokale Pruefung ohne Testcontainers:

```bash
./course-state validate --skip-slow
```

Die Definitionen liegen in `course/states.yml`. Die Overlays bestehen aus normalen Dateien unter `course/overlays/`.
