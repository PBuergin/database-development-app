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

    @Column(name = "external_reference")
    private String externalReference;

    @Column(name = "priority")
    private String priority;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    TicketEntity(String title, String status) {
        this(title, status, "normal", null);
    }

    TicketEntity(String title, String status, String priority, String externalReference) {
        this.title = title;
        this.status = status;
        this.priority = priority;
        this.externalReference = externalReference;
    }

    @PrePersist
    void markCreated() {
        if (updatedAt == null) {
            updatedAt = OffsetDateTime.now();
        }
    }

    @PreUpdate
    void markUpdated() {
        updatedAt = OffsetDateTime.now();
    }

    void markDeleted() {
        deletedAt = OffsetDateTime.now();
    }
}
