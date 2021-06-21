package com.singfung.blackfriday.service.impl;

import com.singfung.blackfriday.dao.OrderDAO;
import com.singfung.blackfriday.model.Order;
import com.singfung.blackfriday.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService
{
	@Autowired
	private OrderDAO orderDAO;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void batchSave(List<Order> orders)
	{
		orderDAO.batchInsert(orders);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteAll()
	{
		orderDAO.deleteAll();
	}
}