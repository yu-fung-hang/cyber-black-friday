package com.singfung.blackfriday.dao;

import org.springframework.stereotype.Repository;
import com.singfung.blackfriday.model.Order;

import java.util.List;

@Repository
public interface OrderDAO
{
	int batchInsert(List<Order> orders);

	int deleteAll();

	int insert(Order order);
}
