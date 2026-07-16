package face_recognition.service;

import face_recognition.dto.request.RegisterRequest;
import face_recognition.dto.response.PagedResponse;
import face_recognition.dto.response.UserResponse;
import face_recognition.model.ClassRoom;
import face_recognition.model.Subject;
import face_recognition.model.User;
import face_recognition.repository.SubjectRepository;
import face_recognition.repository.UserRepository;
import face_recognition.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SubjectRepository subjectRepository;
    private final ClassRoomService classRoomService;



    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("Username đã tồn tại!");

        if (request.getFullName() == null || request.getFullName().isBlank())
            throw new RuntimeException("Họ tên sinh viên không được để trống!");
        if (request.getStudentCode() == null || request.getStudentCode().isBlank())
            throw new RuntimeException("Mã sinh viên không được để trống!");
        if (request.getClassRoomId() == null)
            throw new RuntimeException("Phải chọn lớp!");

        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            if (!request.getPhone().matches("^[0-9]{10,11}$")) {
                throw new RuntimeException("Số điện thoại không hợp lệ! Phải là 10-11 chữ số.");
            }
        }

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new RuntimeException("Email không hợp lệ!");
            }
        }

        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setEmail(request.getEmail());
            user.setFullName(request.getFullName());
            user.setRole(User.Role.USER);
            user.setIsLocked(false);
            User savedUser = userRepository.save(user);

            Integer maxLabel = subjectRepository.findMaxFaceLabel();
            int nextLabel = (maxLabel == null) ? 1 : maxLabel + 1;

            Subject subject = new Subject();
            subject.setFullName(request.getFullName());
            subject.setStudentCode(request.getStudentCode());
            subject.setDateOfBirth(request.getDateOfBirth());
            subject.setPhone(request.getPhone());
            subject.setEmail(request.getEmail());
            subject.setAddress(request.getAddress());
            subject.setNotes(request.getNotes());
            subject.setFaceLabel(nextLabel);
            subject.setFaceImagesPath("./faces_db/subject_" + nextLabel + "/");
            subject.setIsTrained(false);
            subject.setLinkedUser(savedUser);

            ClassRoom classRoom = classRoomService.findById(request.getClassRoomId());
            subject.setClassRoom(classRoom);

            subjectRepository.save(subject);

            return toDTO(savedUser);

        } catch (Exception e) {
            throw new RuntimeException("Đăng ký thất bại: " + e.getMessage());
        }
    }

    public face_recognition.dto.response.PagedResponse<UserResponse> getAll(String search, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Specification<User> spec = UserSpecification.filter(search);
        Page<User> result = userRepository.findAll(spec, pageable);
        return PagedResponse.of(result, this::toDTO);
    }

    public void lockUser(Long id) {
        User user = findById(id);
        if (isAdmin(user)) throw new RuntimeException("Không thể khóa tài khoản ADMIN!");
        user.setIsLocked(true);
        userRepository.save(user);
    }

    public void unlockUser(Long id) {
        User user = findById(id);
        user.setIsLocked(false);
        userRepository.save(user);
    }

    public void delete(Long id) {
        User user = findById(id);
        if (isAdmin(user)) throw new RuntimeException("Không thể xóa tài khoản ADMIN!");
        userRepository.deleteById(id);
    }

    // ── map ──────────────────────────────────────────────

    public UserResponse toDTO(User entity) {
        return UserResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .role(entity.getRole() != null ? entity.getRole().name() : null)
                .isLocked(entity.getIsLocked())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    // ── internal ─────────────────────────────────────────

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user id: " + id));
    }

    private boolean isAdmin(User user) {
        return user.getRole() != null && user.getRole().name().equals("ADMIN");
    }
}