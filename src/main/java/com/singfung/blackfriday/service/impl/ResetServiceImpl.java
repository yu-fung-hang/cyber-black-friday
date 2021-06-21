package com.singfung.blackfriday.service.impl;

import com.singfung.blackfriday.service.OrderService;
import com.singfung.blackfriday.service.ResetService;
import com.singfung.blackfriday.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResetServiceImpl implements ResetService
{
	@Autowired
	private StockService stockService = null;
	@Autowired
	private OrderService orderService = null;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void reset()
	{
		stockService.deleteAll();
		orderService.deleteAll();
	}
}