package ch.hftm.db2.ticketsystem.ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.web.server.ResponseStatusException;

class TicketServiceTest {

    private final TicketRepository ticketRepository = mock(TicketRepository.class);
    private final TicketCommentRepository commentRepository = mock(TicketCommentRepository.class);
    private final TicketEventRepository eventRepository = mock(TicketEventRepository.class);
    private final TicketMapper ticketMapper = Mappers.getMapper(TicketMapper.class);
    private final TicketService ticketService = new TicketService(
            ticketRepository,
            commentRepository,
            eventRepository,
            ticketMapper
    );

    @Test
    void usesActiveStatusRepositoryMethodWhenStatusFilterIsPresent() {
        when(ticketRepository.findByStatusAndDeletedAtIsNullOrderByCreatedAtDesc("open"))
                .thenReturn(List.of(new TicketEntity("VPN pruefen", "open", "normal", null)));

        List<TicketResponse> tickets = ticketService.findTickets("open");

        assertThat(tickets).extracting(TicketResponse::title).containsExactly("VPN pruefen");
        verify(ticketRepository).findByStatusAndDeletedAtIsNullOrderByCreatedAtDesc("open");
    }

    @Test
    void savesTicketCommentEventPriorityAndExternalReferenceWhenCreatingTicket() {
        when(ticketRepository.save(any(TicketEntity.class)))
                .thenAnswer(invocation -> {
                    TicketEntity ticket = invocation.getArgument(0);
                    ticket.setId(42L);
                    ticket.setVersion(0L);
                    return ticket;
                });

        TicketResponse ticket = ticketService.createTicket(
                new CreateTicketRequest("API pruefen", "open", "urgent", " MON-2026-0042 ", "Startkommentar")
        );

        assertThat(ticket.title()).isEqualTo("API pruefen");
        assertThat(ticket.status()).isEqualTo("open");
        assertThat(ticket.priority()).isEqualTo("urgent");
        assertThat(ticket.externalReference()).isEqualTo("MON-2026-0042");
        verify(commentRepository).save(any(TicketCommentEntity.class));
        verify(eventRepository).save(any(TicketEventEntity.class));
    }

    @Test
    void changesStatusAndWritesEvent() {
        TicketEntity ticket = new TicketEntity("VPN pruefen", "open");
        ticket.setId(7L);
        ticket.setVersion(0L);

        when(ticketRepository.findById(7L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.saveAndFlush(any(TicketEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketResponse response = ticketService.changeStatus(
                7L,
                new StatusChangeRequest("waiting", "Rueckfrage gesendet")
        );

        assertThat(response.status()).isEqualTo("waiting");
        verify(eventRepository).save(any(TicketEventEntity.class));
    }

    @Test
    void rejectsInvalidPriority() {
        assertThatThrownBy(() -> ticketService.createTicket(
                new CreateTicketRequest("API pruefen", "open", "critical", null, null)
        )).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void marksTicketAsDeletedInsteadOfRemovingIt() {
        TicketEntity ticket = new TicketEntity("VPN pruefen", "open");
        ticket.setId(7L);

        when(ticketRepository.findById(7L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.saveAndFlush(ticket)).thenReturn(ticket);

        ticketService.deleteTicket(7L);

        assertThat(ticket.getDeletedAt()).isNotNull();
        verify(ticketRepository).saveAndFlush(ticket);
    }

    @Test
    void rejectsStatusChangeForDeletedTicket() {
        TicketEntity ticket = new TicketEntity("VPN pruefen", "open");
        ticket.setId(7L);
        ticket.markDeleted();

        when(ticketRepository.findById(7L)).thenReturn(Optional.of(ticket));

        assertThatThrownBy(() -> ticketService.changeStatus(7L, new StatusChangeRequest("waiting", null)))
                .isInstanceOf(ResponseStatusException.class);
    }
}
