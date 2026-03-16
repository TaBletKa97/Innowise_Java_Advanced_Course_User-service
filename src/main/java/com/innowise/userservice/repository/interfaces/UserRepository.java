package com.innowise.userservice.repository.interfaces;

import org.springframework.data.jpa.repository.*;

import com.innowise.userservice.repository.entity.User;
import com.innowise.userservice.repository.exceptions.ActivationException;
import com.innowise.userservice.repository.exceptions.DeactivationException;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>,
        JpaSpecificationExecutor<User> {

    @EntityGraph(attributePaths = "cards")
    List<User> findAll();

    @EntityGraph(attributePaths = "cards")
    Optional<User> findById(Long id);

    @EntityGraph(attributePaths = "cards")
    Page<User> findAll(Specification<User> spec, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = "cards")
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdWithLock(Long id);

    default User activateUser(Long id) throws ActivationException {
        User user = findById(id).orElseThrow();
        if (user.isActive()) {
            throw new ActivationException();
        }
        user.setActive(true);
        return user;
    }

    default User deactivateUser(Long id) {
        User user = findById(id).orElseThrow();
        if (!user.isActive()) {
            throw new DeactivationException();
        }
        user.setActive(false);
        return user;
    }
}
