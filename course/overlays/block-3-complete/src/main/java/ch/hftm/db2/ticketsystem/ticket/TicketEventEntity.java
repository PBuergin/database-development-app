package ch.hftm.db2.ticketsystem.ticket;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "ticket_events", schema = "app_starter")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class TicketEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id")
    private Long ticketId;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "old_status")
    private String oldStatus;

    @Column(name = "new_status")
    private String newStatus;

    @Column(name = "reason")
    private String reason;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    TicketEventEntity(Long ticketId, String eventType, String oldStatus, String newStatus, String reason) {
        this.ticketId = ticketId;
        this.eventType = eventType;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.reason = reason;
    }
}
