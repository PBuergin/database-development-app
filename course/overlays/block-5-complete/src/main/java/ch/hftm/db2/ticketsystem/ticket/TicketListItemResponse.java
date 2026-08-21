package ch.hftm.db2.ticketsystem.ticket;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import java.time.OffsetDateTime;

@Value
@Schema(description = "Schlanke Antwort fuer eine Ticketuebersicht")
public class TicketListItemResponse {

    @Schema(description = "Technische Ticket-ID", example = "1")
    Long id;

    @Schema(description = "Kurzer Tickettitel", example = "VPN-Zugriff analysieren")
    String title;

    @Schema(description = "Aktueller Ticketstatus", example = "open")
    String status;

    @Schema(description = "Prioritaet", example = "normal")
    String priority;

    @Schema(description = "Erstellzeitpunkt des Tickets")
    OffsetDateTime createdAt;
}
