package vn.security.social.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.security.social.model.User;
import vn.security.social.repository.IUserRepository;
import vn.security.social.service.IUserService;


@Service
public class UserService implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    @Override
    public User save(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public User getByUsername(String username) {
        return this.userRepository.findByAccount_Username(username);
    }
}