package vn.security.social.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.security.social.model.Account;

public interface IAccountRepository extends JpaRepository<Account, String> {

    Boolean existsByUsername(String username);

}