package face_recognition.model;
import jakarta.persistence.*;
import org.springframework.web.bind.support.SessionStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String action;           // "CREATE", "UPDATE", "DELETE", "LOGIN"
    private String entityType;       // "Subject", "User", "AttendanceSession"
    private Long entityId;
    private String oldValue;         // JSON trước khi sửa
    private String newValue;         // JSON sau khi sửa
    private String ipAddress;
    private LocalDateTime createdAt;
}
