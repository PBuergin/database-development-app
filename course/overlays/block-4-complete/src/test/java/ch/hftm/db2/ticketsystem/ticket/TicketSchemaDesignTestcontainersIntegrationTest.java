package ch.hftm.db2.ticketsystem.ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tag("testcontainers")
@Testcontainers
@SpringBootTest
class TicketSchemaDesignTestcontainersIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("ticket_system")
            .withUsername("ticket_user")
            .withPassword("ticket_user");

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @DynamicPropertySource
    static void configurePostgresql(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void createsTicketWithSchemaDesignFields() {
        TicketResponse ticket = ticketService.createTicket(
                new CreateTicketRequest("Schema pruefen", "open", "urgent", "MON-2026-0042", null)
        );

        assertThat(ticket.id()).isNotNull();
        assertThat(ticket.priority()).isEqualTo("urgent");
        assertThat(ticket.externalReference()).isEqualTo("MON-2026-0042");
        assertThat(ticket.updatedAt()).isNotNull();
    }

    @Test
    void rejectsDuplicateExternalReferenceInDatabase() {
        ticketService.createTicket(
                new CreateTicketRequest("Erstes Ticket", "open", "normal", "MON-2026-0099", null)
        );

        assertThatThrownBy(() -> ticketService.createTicket(
                new CreateTicketRequest("Zweites Ticket", "open", "normal", "MON-2026-0099", null)
        )).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void softDeletedTicketIsHiddenFromListButStillStored() {
        TicketResponse ticket = ticketService.createTicket(
                new CreateTicketRequest("Soft Delete pruefen", "open", "normal", "MON-2026-0100", null)
        );

        ticketService.deleteTicket(ticket.id());

        assertThat(ticketService.findTickets(null))
                .extracting(TicketResponse::id)
                .doesNotContain(ticket.id());

        TicketEntity storedTicket = ticketRepository.findById(ticket.id()).orElseThrow();
        assertThat(storedTicket.getDeletedAt()).isNotNull();
    }
}
