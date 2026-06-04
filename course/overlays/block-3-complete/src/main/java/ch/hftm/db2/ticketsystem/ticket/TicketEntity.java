package ch.hftm.db2.ticketsystem.ticket;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tickets", schema = "app_starter")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "status")
    private String status;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    TicketEntity(String title, String status) {
        this.title = title;
        this.status = status;
    }
}
