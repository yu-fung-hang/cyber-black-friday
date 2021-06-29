package com.singfung.blackfriday.service.impl;

import com.singfung.blackfriday.dao.OrderDAO;
import com.singfung.blackfriday.model.Order;
import com.singfung.blackfriday.model.Stock;
import com.singfung.blackfriday.service.OrderService;

import com.singfung.blackfriday.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService
{
	@Autowired
	private OrderDAO orderDAO;
	@Autowired
	private StockService stockService;

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

	@Override
	@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public boolean generateAnOrderWithPessimisticLocking(int stockId, int userId)
	{
		Stock stock = stockService.selectForUpdate(stockId);

		if(stock.getStockNum() > 0)
		{
			int result = stockService.decreaseStockNumByOne(stock);

			Order order = new Order();
			order.setUserId(userId);
			order.setProductId(stockId);
			order.setItemsNum(1);
			Timestamp currentTime = new Timestamp(System.currentTimeMillis());
			order.setOrderedTime(currentTime);
			order.setDbCreatedTime(currentTime);

			orderDAO.insert(order);
			return true;
		}

		System.out.println("Orders are full!");
		return false;
	}
}