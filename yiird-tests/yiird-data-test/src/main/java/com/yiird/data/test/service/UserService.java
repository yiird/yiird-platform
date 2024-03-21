package com.yiird.data.test.service;

import com.yiird.data.test.model.user.User;
import com.yiird.data.test.repo.user.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Transactional
    public void createUser() {
        User user = new User();
        user.setName("Lou Fei");
        user.setAge(18);
        userRepo.save(user);
    }
}
