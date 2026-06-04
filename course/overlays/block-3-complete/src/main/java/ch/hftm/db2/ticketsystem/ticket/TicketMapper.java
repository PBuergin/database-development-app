package ch.hftm.db2.ticketsystem.ticket;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
interface TicketMapper {

    TicketResponse toResponse(TicketEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    TicketEntity toEntity(CreateTicketRequest request);
}
