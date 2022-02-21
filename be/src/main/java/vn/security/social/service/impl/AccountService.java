package vn.security.social.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.security.social.model.Account;
import vn.security.social.repository.IAccountRepository;
import vn.security.social.service.IAccountService;

import java.util.Optional;


@Service
public class AccountService implements IAccountService {

    @Autowired
    private IAccountRepository accountRepository;


    @Override
    public Optional<Account> getById(String username) {
        return this.accountRepository.findById(username);
    }

    @Override
    public Boolean isUsernameExists(String username) {
        return this.accountRepository.existsByUsername(username);
    }
}
