package ch.hftm.db2.ticketsystem.ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class TicketQueryDesignServiceTest {

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
    void normalizesOverviewStatusBeforeCallingJpqlProjection() {
        TicketListItemResponse item = new TicketListItemResponse(
                7L,
                "VPN pruefen",
                "open",
                "normal",
                OffsetDateTime.now()
        );
        when(ticketRepository.findActiveTicketOverview("open")).thenReturn(List.of(item));

        List<TicketListItemResponse> result = ticketService.findTicketOverview(" OPEN ");

        assertThat(result).containsExactly(item);
        verify(ticketRepository).findActiveTicketOverview("open");
    }

    @Test
    void passesNullToJpqlProjectionWhenNoStatusFilterIsPresent() {
        when(ticketRepository.findActiveTicketOverview(null)).thenReturn(List.of());

        assertThat(ticketService.findTicketOverview(null)).isEmpty();

        verify(ticketRepository).findActiveTicketOverview(null);
    }

    @Test
    void mapsNativeProjectionToExplicitResponseDto() {
        TicketStatusPriorityReportProjection row = new TicketStatusPriorityReportProjection() {
            @Override
            public String getStatus() {
                return "open";
            }

            @Override
            public String getPriority() {
                return "high";
            }

            @Override
            public long getTicketCount() {
                return 2;
            }

            @Override
            public long getCommentCount() {
                return 3;
            }
        };
        when(ticketRepository.findActiveStatusPriorityReport()).thenReturn(List.of(row));

        List<TicketStatusPriorityReportResponse> result = ticketService.findStatusPriorityReport();

        assertThat(result).extracting(
                TicketStatusPriorityReportResponse::getStatus,
                TicketStatusPriorityReportResponse::getPriority,
                TicketStatusPriorityReportResponse::getTicketCount,
                TicketStatusPriorityReportResponse::getCommentCount
        ).containsExactly(org.assertj.core.groups.Tuple.tuple("open", "high", 2L, 3L));
        verify(ticketRepository).findActiveStatusPriorityReport();
    }
}
