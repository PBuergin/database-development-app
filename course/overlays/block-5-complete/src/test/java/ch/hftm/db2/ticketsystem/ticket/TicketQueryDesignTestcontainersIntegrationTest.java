package ch.hftm.db2.ticketsystem.ticket;

import static org.assertj.core.api.Assertions.assertThat;

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
class TicketQueryDesignTestcontainersIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("ticket_system")
            .withUsername("ticket_user")
            .withPassword("ticket_user");

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketCommentRepository commentRepository;

    @DynamicPropertySource
    static void configurePostgresql(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void jpqlOverviewFiltersStatusAndExcludesSoftDeletedTickets() {
        TicketResponse visible = ticketService.createTicket(
                new CreateTicketRequest("Offenes Ticket", "open", "high", "QRY-2026-0001", null)
        );
        TicketResponse deleted = ticketService.createTicket(
                new CreateTicketRequest("Ausgeblendetes Ticket", "open", "normal", "QRY-2026-0002", null)
        );
        ticketService.createTicket(
                new CreateTicketRequest("Wartendes Ticket", "waiting", "normal", "QRY-2026-0003", null)
        );
        ticketService.deleteTicket(deleted.id());

        assertThat(ticketService.findTicketOverview("open"))
                .extracting(TicketListItemResponse::getId)
                .contains(visible.id())
                .doesNotContain(deleted.id());
    }

    @Test
    void nativeReportCountsTicketsOnceAndCommentsSeparately() {
        TicketResponse first = ticketService.createTicket(
                new CreateTicketRequest("Erstes Report-Ticket", "open", "high", "QRY-2026-0011", "Erster Kommentar")
        );
        commentRepository.save(new TicketCommentEntity(first.id(), "Zweiter Kommentar"));
        TicketResponse second = ticketService.createTicket(
                new CreateTicketRequest("Zweites Report-Ticket", "open", "high", "QRY-2026-0012", null)
        );
        ticketService.deleteTicket(second.id());

        assertThat(ticketService.findStatusPriorityReport())
                .filteredOn(row -> row.getStatus().equals("open") && row.getPriority().equals("high"))
                .extracting(
                        TicketStatusPriorityReportResponse::getTicketCount,
                        TicketStatusPriorityReportResponse::getCommentCount
                )
                .contains(org.assertj.core.groups.Tuple.tuple(1L, 2L));
    }
}
