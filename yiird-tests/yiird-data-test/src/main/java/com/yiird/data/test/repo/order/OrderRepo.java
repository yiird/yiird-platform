package com.yiird.data.test.repo.order;

import com.yiird.data.test.model.order.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepo extends CrudRepository<Order,Long> {

}
