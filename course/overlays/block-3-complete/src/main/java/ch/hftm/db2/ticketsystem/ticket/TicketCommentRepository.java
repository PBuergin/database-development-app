package ch.hftm.db2.ticketsystem.ticket;

import org.springframework.data.jpa.repository.JpaRepository;

interface TicketCommentRepository extends JpaRepository<TicketCommentEntity, Long> {

    long countByTicketId(Long ticketId);
}
