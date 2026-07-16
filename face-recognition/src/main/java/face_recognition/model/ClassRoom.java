package face_recognition.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "class_rooms")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ClassRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, length = 50)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String teacher;

    @Column(name = "max_students")
    private Integer maxStudents;

    @OneToMany(mappedBy = "classRoom")
    private List<Subject> subjects;

    @OneToMany(mappedBy = "classRoom")
    private List<AttendanceSession> sessions;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}