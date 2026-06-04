package ch.hftm.db2.ticketsystem.ticket;

import org.springframework.data.jpa.repository.JpaRepository;

interface TicketEventRepository extends JpaRepository<TicketEventEntity, Long> {

    long countByTicketId(Long ticketId);
}
