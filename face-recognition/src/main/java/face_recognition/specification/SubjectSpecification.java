// SubjectSpecification.java
package face_recognition.specification;

import face_recognition.model.Subject;
import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

@UtilityClass
public class SubjectSpecification {

    public Specification<Subject> filter(String search, Long classId) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (StringUtils.hasText(search)) {
                String like = "%" + search.toLowerCase() + "%";
                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(root.get("fullName")),    like),
                        cb.like(cb.lower(root.get("studentCode")), like),
                        cb.like(cb.lower(root.get("phone")),       like),
                        cb.like(cb.lower(root.get("email")),       like)
                ));
            }

            if (classId != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("classRoom").get("id"), classId)
                );
            }

            return predicate;
        };
    }
}