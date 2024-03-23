package com.yiird.data.test.service;

import com.yiird.data.test.model.order.Order;
import com.yiird.data.test.repo.order.OrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {


    private OrderRepo orderRepo;

    @Transactional
    public void createOrder() {
        Order order = new Order();
        order.setNo(1);
        order.setPrice(2);
        orderRepo.save(order);
    }

    @Autowired
    public void setOrderRepo(OrderRepo orderRepo) {
        this.orderRepo = orderRepo;
    }
}
