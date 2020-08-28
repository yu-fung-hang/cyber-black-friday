package com.singfung.blackfriday.service;

import com.singfung.blackfriday.model.Order;

import java.util.List;

public interface OrderService
{	
//	public int generateAnOrder(Long stockId, Long userId);

//	public int generateAnOrderWithOptimLocking(Long stockId, Long userId);

	public Long generateAnOrderInRedis(int redPacketId, int userId);

	void batchSave(List<Order> orders);
}
