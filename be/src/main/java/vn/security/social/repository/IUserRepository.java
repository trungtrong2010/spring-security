package vn.security.social.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.security.social.model.User;

public interface IUserRepository extends JpaRepository<User, String> {
    User findByAccount_Username(String username);
}