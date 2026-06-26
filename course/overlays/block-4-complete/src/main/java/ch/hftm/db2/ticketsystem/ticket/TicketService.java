package ch.hftm.db2.ticketsystem.ticket;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
class TicketService {

    private static final Set<String> VALID_STATUSES = Set.of("open", "waiting", "closed");
    private static final Set<String> VALID_PRIORITIES = Set.of("low", "normal", "high", "urgent");

    private final TicketRepository ticketRepository;
    private final TicketCommentRepository commentRepository;
    private final TicketEventRepository eventRepository;
    private final TicketMapper ticketMapper;

    TicketService(
            TicketRepository ticketRepository,
            TicketCommentRepository commentRepository,
            TicketEventRepository eventRepository,
            TicketMapper ticketMapper
    ) {
        this.ticketRepository = ticketRepository;
        this.commentRepository = commentRepository;
        this.eventRepository = eventRepository;
        this.ticketMapper = ticketMapper;
    }

    @Transactional(readOnly = true)
    List<TicketResponse> findTickets(String status) {
        List<TicketEntity> tickets = StringUtils.hasText(status)
                ? ticketRepository.findByStatusAndDeletedAtIsNullOrderByCreatedAtDesc(normalizeStatus(status))
                : ticketRepository.findByDeletedAtIsNullOrderByCreatedAtDesc();

        return tickets.stream()
                .map(ticketMapper::toResponse)
                .toList();
    }

    @Transactional
    TicketResponse createTicket(CreateTicketRequest request) {
        TicketEntity savedTicket = persistTicketWorkflow(request);
        return ticketMapper.toResponse(savedTicket);
    }

    @Transactional
    TicketResponse changeStatus(Long id, StatusChangeRequest request) {
        String newStatus = normalizeStatus(request.getStatus());
        TicketEntity ticket = findActiveTicket(id);

        assertStatusTransition(ticket.getStatus(), newStatus);

        String oldStatus = ticket.getStatus();
        ticket.setStatus(newStatus);
        TicketEntity savedTicket = ticketRepository.saveAndFlush(ticket);
        eventRepository.save(new TicketEventEntity(
                savedTicket.getId(),
                "status_changed",
                oldStatus,
                newStatus,
                StringUtils.hasText(request.getReason()) ? request.getReason() : null
        ));

        return ticketMapper.toResponse(savedTicket);
    }

    @Transactional
    void deleteTicket(Long id) {
        TicketEntity ticket = findActiveTicket(id);
        ticket.markDeleted();
        ticketRepository.saveAndFlush(ticket);
    }

    @Transactional
    void createTicketAndFailForRollbackProbe(CreateTicketRequest request) {
        persistTicketWorkflow(request);
        throw new IllegalStateException("Rollback-Probe nach Datenbankschritten");
    }

    private TicketEntity persistTicketWorkflow(CreateTicketRequest request) {
        String status = normalizeStatus(request.getStatus());
        String priority = normalizePriority(request.getPriority());
        String externalReference = normalizeOptional(request.getExternalReference());

        TicketEntity entity = new TicketEntity(request.getTitle(), status, priority, externalReference);
        TicketEntity savedTicket = ticketRepository.save(entity);

        if (StringUtils.hasText(request.getInitialComment())) {
            commentRepository.save(new TicketCommentEntity(savedTicket.getId(), request.getInitialComment()));
        }

        eventRepository.save(new TicketEventEntity(
                savedTicket.getId(),
                "ticket_created",
                null,
                status,
                "Ticket erstellt"
        ));

        return savedTicket;
    }

    private TicketEntity findActiveTicket(Long id) {
        TicketEntity ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket nicht gefunden"));
        if (ticket.getDeletedAt() != null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket nicht gefunden");
        }
        return ticket;
    }

    private String normalizeStatus(String status) {
        String normalized = status == null ? "" : status.trim().toLowerCase();
        if (!VALID_STATUSES.contains(normalized)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ungueltiger Ticketstatus");
        }
        return normalized;
    }

    private String normalizePriority(String priority) {
        String normalized = StringUtils.hasText(priority) ? priority.trim().toLowerCase() : "normal";
        if (!VALID_PRIORITIES.contains(normalized)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ungueltige Prioritaet");
        }
        return normalized;
    }

    private String normalizeOptional(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private void assertStatusTransition(String oldStatus, String newStatus) {
        if (oldStatus.equals(newStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status ist bereits gesetzt");
        }
        if ("closed".equals(oldStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geschlossene Tickets werden nicht wieder geoeffnet");
        }
    }
}
