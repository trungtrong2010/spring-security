package vn.security.social.service;

import org.springframework.stereotype.Service;
import vn.security.social.model.User;

@Service
public interface IUserService {

    User getByUsername(String username);

    User save(User user);

}
