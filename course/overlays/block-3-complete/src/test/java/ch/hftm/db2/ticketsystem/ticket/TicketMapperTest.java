package ch.hftm.db2.ticketsystem.ticket;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class TicketMapperTest {

    private final TicketMapper mapper = Mappers.getMapper(TicketMapper.class);

    @Test
    void mapsCreateRequestToEntity() {
        TicketEntity entity = mapper.toEntity(new CreateTicketRequest("VPN pruefen", "open", "Startkommentar"));

        assertThat(entity.getTitle()).isEqualTo("VPN pruefen");
        assertThat(entity.getStatus()).isEqualTo("open");
        assertThat(entity.getVersion()).isNull();
    }
}
