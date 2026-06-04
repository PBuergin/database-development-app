package ch.hftm.db2.ticketsystem.ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tag("testcontainers")
@Testcontainers
@SpringBootTest
class TicketWorkflowTestcontainersIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("ticket_system")
            .withUsername("ticket_user")
            .withPassword("ticket_user");

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketCommentRepository commentRepository;

    @Autowired
    private TicketEventRepository eventRepository;

    @DynamicPropertySource
    static void configurePostgresql(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void createsTicketCommentAndEventInOneWorkflow() {
        TicketResponse ticket = ticketService.createTicket(
                new CreateTicketRequest("Transaktion pruefen", "open", "Startkommentar")
        );

        assertThat(ticket.id()).isNotNull();
        assertThat(ticket.version()).isZero();
        assertThat(commentRepository.countByTicketId(ticket.id())).isEqualTo(1);
        assertThat(eventRepository.countByTicketId(ticket.id())).isEqualTo(1);
    }

    @Test
    void rollsBackCompleteWorkflowWhenRuntimeExceptionEscapesTransaction() {
        long ticketsBefore = ticketRepository.count();
        long commentsBefore = commentRepository.count();
        long eventsBefore = eventRepository.count();

        assertThatThrownBy(() -> ticketService.createTicketAndFailForRollbackProbe(
                new CreateTicketRequest("Rollback pruefen", "open", "Wird zurueckgerollt")
        )).isInstanceOf(IllegalStateException.class);

        assertThat(ticketRepository.count()).isEqualTo(ticketsBefore);
        assertThat(commentRepository.count()).isEqualTo(commentsBefore);
        assertThat(eventRepository.count()).isEqualTo(eventsBefore);
    }

    @Test
    void statusChangeWritesEventAndIncrementsVersion() {
        TicketResponse ticket = ticketService.createTicket(
                new CreateTicketRequest("Statuswechsel pruefen", "open", null)
        );

        TicketResponse changed = ticketService.changeStatus(
                ticket.id(),
                new StatusChangeRequest("waiting", "Rueckfrage gesendet")
        );

        assertThat(changed.status()).isEqualTo("waiting");
        assertThat(changed.version()).isGreaterThan(ticket.version());
        assertThat(eventRepository.countByTicketId(ticket.id())).isEqualTo(2);
    }
}
