package com.innowise.userservice.model.interfaces;

import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.model.exceptions.ActivationException;
import com.innowise.userservice.model.exceptions.DeactivationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>,
        JpaSpecificationExecutor<User> {

    List<User> findUserByNameAndSurname(String name, String surname);

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
