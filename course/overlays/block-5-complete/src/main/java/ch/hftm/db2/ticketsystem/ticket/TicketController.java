package ch.hftm.db2.ticketsystem.ticket;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Tickets", description = "Ticket Controller")
class TicketController {

    private final TicketService ticketService;

    TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    @Operation(summary = "Tickets lesen", description = "Liest alle nicht geloeschten Tickets oder filtert nach einem Status.")
    List<TicketResponse> findTickets(
            @Parameter(description = "Optionaler Statusfilter, zum Beispiel open")
            @RequestParam(required = false) String status
    ) {
        return ticketService.findTickets(status);
    }

    @GetMapping("/overview")
    @Operation(summary = "Schlanke Ticketuebersicht lesen", description = "Liest mit JPQL nur die Felder, die eine Ticketliste braucht.")
    List<TicketListItemResponse> findTicketOverview(
            @Parameter(description = "Optionaler Statusfilter, zum Beispiel open")
            @RequestParam(required = false) String status
    ) {
        return ticketService.findTicketOverview(status);
    }

    @GetMapping("/reports/status-priority")
    @Operation(summary = "Ticketreport nach Status und Prioritaet", description = "Liest eine native SQL-Aggregation mit Ticket- und Kommentaranzahl.")
    List<TicketStatusPriorityReportResponse> findStatusPriorityReport() {
        return ticketService.findStatusPriorityReport();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Ticket erstellen", description = "Erstellt ein Ticket mit Prioritaet, optionaler fachlicher Referenz und optionalem ersten Kommentar.")
    TicketResponse createTicket(@Valid @RequestBody CreateTicketRequest request) {
        return ticketService.createTicket(request);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Ticketstatus aendern", description = "Aendert den Status und schreibt ein Ereignis.")
    TicketResponse changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusChangeRequest request
    ) {
        return ticketService.changeStatus(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Ticket ausblenden", description = "Markiert ein Ticket mit deleted_at, statt die Zeile physisch zu loeschen.")
    void deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
    }
}
