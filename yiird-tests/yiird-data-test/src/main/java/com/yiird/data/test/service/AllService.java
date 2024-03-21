package com.yiird.data.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AllService {

    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;

    @Transactional
    public void createAll() {
        userService.createUser();
        orderService.createOrder();
    }
}
