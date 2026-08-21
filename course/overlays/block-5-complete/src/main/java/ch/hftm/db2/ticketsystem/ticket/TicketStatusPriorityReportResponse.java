package ch.hftm.db2.ticketsystem.ticket;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

@Value
@Schema(description = "Aggregierte Ticketzahlen nach Status und Prioritaet")
public class TicketStatusPriorityReportResponse {

    @Schema(description = "Ticketstatus", example = "open")
    String status;

    @Schema(description = "Prioritaet", example = "high")
    String priority;

    @Schema(description = "Anzahl aktiver Tickets", example = "3")
    long ticketCount;

    @Schema(description = "Anzahl Kommentare zu diesen Tickets", example = "5")
    long commentCount;
}
