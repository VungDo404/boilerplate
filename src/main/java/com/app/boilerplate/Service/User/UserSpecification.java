package com.app.boilerplate.Service.User;

import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Domain.User.User_;
import com.app.boilerplate.Shared.User.Dto.UserCriteriaDto;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Optional;

public class UserSpecification {
    static Specification<User> hasUserNameLike(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .like(root.<String>get(User_.USERNAME), "%" + name + "%");
    }

    static Specification<User> hasEmailLike(String email) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.<String>get(User_.EMAIL),
                "%" + email + "%");
    }

    static Specification<User> hasEmailSpecifyAfter(LocalDateTime after) {
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .greaterThanOrEqualTo(root.<LocalDateTime>get(User_.EMAIL_SPECIFY), after);
    }

    static Specification<User> hasEmailSpecifyBefore(LocalDateTime before) {
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .lessThanOrEqualTo(root.<LocalDateTime>get(User_.EMAIL_SPECIFY), before);
    }

    public static Specification<User> specification(Optional<UserCriteriaDto> userCriteriaDto) {
        Specification<User> specification = Specification.where(null);
        if (userCriteriaDto.isPresent()) {
            if (userCriteriaDto.get().getEmail() != null)
                specification =
                        specification.and(hasEmailLike(userCriteriaDto.get().getEmail()));

            if (userCriteriaDto.get().getUsername() != null)
                specification =
                        specification
                                .and(hasUserNameLike(userCriteriaDto.get().getUsername()));

            if (userCriteriaDto.get().getEmailSpecifyAfter() != null)
                specification = specification
                        .and(hasEmailSpecifyAfter(
                                userCriteriaDto.get().getEmailSpecifyAfter()));
        }
        return specification;
    }
}
