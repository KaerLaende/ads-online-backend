package ru.skypro.ads.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.ads.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User getUserByEmail(String email);

    Optional<User> findUserByEmail(String email);
}