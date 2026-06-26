package ch.hftm.db2.ticketsystem.ticket;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Eingabe für ein neues Ticket")
class CreateTicketRequest {

    @NotBlank
    @Schema(description = "Kurzer Tickettitel", example = "Datenbankverbindung pruefen")
    private String title;

    @NotBlank
    @Schema(description = "Ticketstatus", example = "open")
    private String status;

    @Schema(description = "Priorität des Tickets", example = "normal")
    private String priority;

    @Schema(description = "Optionale fachliche Referenz aus einem externen System", example = "MON-2026-0042")
    private String externalReference;

    @Schema(description = "Optionaler erster Kommentar", example = "Fehler tritt seit heute Morgen auf.")
    private String initialComment;

    CreateTicketRequest(String title, String status, String initialComment) {
        this(title, status, "normal", null, initialComment);
    }

    CreateTicketRequest(
            String title,
            String status,
            String priority,
            String externalReference,
            String initialComment
    ) {
        this.title = title;
        this.status = status;
        this.priority = priority;
        this.externalReference = externalReference;
        this.initialComment = initialComment;
    }
}
