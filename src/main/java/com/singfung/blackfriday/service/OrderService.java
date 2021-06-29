package com.singfung.blackfriday.service;

import com.singfung.blackfriday.model.Order;

import java.util.List;

public interface OrderService
{
	void batchSave(List<Order> orders);

	void deleteAll();

	boolean generateAnOrderWithPessimisticLocking(int stockId, int userId);
}
