package face_recognition.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private AttendanceSession session;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;

    private LocalDateTime checkedAt;

    private Double confidence;

    private String method;

    @PrePersist
    public void prePersist() {
        if (checkedAt == null) checkedAt = LocalDateTime.now();
        if (status == null) status = AttendanceStatus.PRESENT;
    }

    public enum AttendanceStatus {
        PRESENT, ABSENT, LATE, EXCUSED
    }
}