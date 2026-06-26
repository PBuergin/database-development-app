package ch.hftm.db2.ticketsystem.ticket;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import java.time.OffsetDateTime;

@Value
@Schema(description = "Antwortmodell für ein Ticket")
class TicketResponse {

    @Schema(description = "Technische Ticket-ID", example = "1")
    Long id;

    @Schema(description = "Kurzer Tickettitel", example = "VPN-Zugriff analysieren")
    String title;

    @Schema(description = "Aktueller Ticketstatus", example = "open")
    String status;

    @Schema(description = "Priorität", example = "normal")
    String priority;

    @Schema(description = "Optionale fachliche Referenz", example = "MON-2026-0042")
    String externalReference;

    @Schema(description = "Optimistic-Locking-Version", example = "0")
    Long version;

    @Schema(description = "Erstellzeitpunkt des Tickets")
    OffsetDateTime createdAt;

    @Schema(description = "Letzter Änderungszeitpunkt des Tickets")
    OffsetDateTime updatedAt;

    Long id() {
        return id;
    }

    String title() {
        return title;
    }

    String status() {
        return status;
    }

    String priority() {
        return priority;
    }

    String externalReference() {
        return externalReference;
    }

    Long version() {
        return version;
    }

    OffsetDateTime createdAt() {
        return createdAt;
    }

    OffsetDateTime updatedAt() {
        return updatedAt;
    }
}
