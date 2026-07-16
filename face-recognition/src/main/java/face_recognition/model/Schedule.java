package face_recognition.model;
import jakarta.persistence.*;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Data
@Table(name = "schedules")
public class Schedule {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassRoom classRoom;

    private String name;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private LocalDate validFrom;
    private LocalDate validTo;
    private Boolean isActive;
}
