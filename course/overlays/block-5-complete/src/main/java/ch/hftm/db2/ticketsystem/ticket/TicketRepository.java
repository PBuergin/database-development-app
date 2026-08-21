package ch.hftm.db2.ticketsystem.ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface TicketRepository extends JpaRepository<TicketEntity, Long> {

    List<TicketEntity> findByDeletedAtIsNullOrderByCreatedAtDesc();

    List<TicketEntity> findByStatusAndDeletedAtIsNullOrderByCreatedAtDesc(String status);

    @Query("""
            SELECT new ch.hftm.db2.ticketsystem.ticket.TicketListItemResponse(
                t.id, t.title, t.status, t.priority, t.createdAt
            )
            FROM TicketEntity t
            WHERE t.deletedAt IS NULL
              AND (:status IS NULL OR t.status = :status)
            ORDER BY t.createdAt DESC
            """)
    List<TicketListItemResponse> findActiveTicketOverview(@Param("status") String status);

    @Query(value = """
            SELECT
                t.status AS status,
                t.priority AS priority,
                COUNT(DISTINCT t.id) AS "ticketCount",
                COUNT(c.id) AS "commentCount"
            FROM app_starter.tickets t
            LEFT JOIN app_starter.ticket_comments c ON c.ticket_id = t.id
            WHERE t.deleted_at IS NULL
            GROUP BY t.status, t.priority
            ORDER BY t.status, t.priority
            """, nativeQuery = true)
    List<TicketStatusPriorityReportProjection> findActiveStatusPriorityReport();
}
