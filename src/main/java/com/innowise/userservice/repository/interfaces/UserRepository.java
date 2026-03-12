package com.innowise.userservice.repository.interfaces;

import com.innowise.userservice.repository.entity.User;
import com.innowise.userservice.repository.exceptions.ActivationException;
import com.innowise.userservice.repository.exceptions.DeactivationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

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
