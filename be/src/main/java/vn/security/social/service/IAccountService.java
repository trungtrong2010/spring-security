package vn.security.social.service;

import org.springframework.stereotype.Service;
import vn.security.social.model.Account;

import java.util.Optional;

@Service
public interface IAccountService {

    Optional<Account> getById(String username);

    Boolean isUsernameExists(String username);
}
