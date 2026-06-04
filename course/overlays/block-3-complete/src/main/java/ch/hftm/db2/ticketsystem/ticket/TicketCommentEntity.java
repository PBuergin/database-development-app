package ch.hftm.db2.ticketsystem.ticket;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "ticket_comments", schema = "app_starter")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class TicketCommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id")
    private Long ticketId;

    @Column(name = "body")
    private String body;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    TicketCommentEntity(Long ticketId, String body) {
        this.ticketId = ticketId;
        this.body = body;
    }
}
