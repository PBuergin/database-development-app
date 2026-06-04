package ch.hftm.db2.ticketsystem.ticket;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "Antwortmodell für ein Ticket")
record TicketResponse(@Schema(description = "Technische Ticket-ID", example = "1") Long id,
                      @Schema(description = "Kurzer Tickettitel", example = "VPN-Zugriff analysieren") String title,
                      @Schema(description = "Aktueller Ticketstatus", example = "open") String status,
                      @Schema(description = "Optimistic-Locking-Version", example = "0") Long version,
                      @Schema(description = "Erstellzeitpunkt des Tickets") OffsetDateTime createdAt) {
}
