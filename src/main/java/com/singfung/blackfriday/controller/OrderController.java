package com.singfung.blackfriday.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.singfung.blackfriday.common.Result;
import com.singfung.blackfriday.exception.BusinessException;
import com.singfung.blackfriday.model.Order;
import com.singfung.blackfriday.model.Stock;
import com.singfung.blackfriday.service.OrderService;
import com.singfung.blackfriday.service.StockRedisService;
import com.singfung.blackfriday.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.sql.Timestamp;

@Controller
@RequestMapping("/blackfriday/order")
public class OrderController
{
	@Autowired
	private OrderService orderService = null;
	@Autowired
	private StockService stockService = null;
	@Autowired
	private StockRedisService stockRedisService = null;

	@PostMapping(value = "/{stockId}/redis-order/{userId}")
	@ResponseBody
	@CrossOrigin
	public Map<String, Object> generateAnOrderInRedis(@PathVariable int stockId, @PathVariable int userId)
	{
		//Check whether this stock exists or not
		stockService.findById(stockId);

		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long result = orderService.generateAnOrderInRedis(stockId, userId);
		boolean flag = result > 0;
		resultMap.put("result", flag);
		resultMap.put("message", flag ? "Success": "Failure");
		return resultMap;
	}

	@PostMapping(value = "/redis-db")
	@ResponseBody
	public void saveOrdersFromRedisToDB(int stockId)
	{
		stockRedisService.saveOrdersFromRedisToDB(stockId);
	}

	@PostMapping("")
	@ResponseBody
	public ResponseEntity<Result<Object>> add(@RequestBody List<Order> orders)
	{
		List<Stock> stock_list = stockService.findAll();
		HashSet<Integer> stock_set = new HashSet<>();
		for(Stock stock : stock_list)
		{ stock_set.add(stock.getId()); }

		for(Order order : orders)
		{
			if(stock_set.contains(order.getProductId()) == false)
			{ throw new BusinessException("Product not found."); }

			if(order.getUserId() <= 0)
			{ throw new BusinessException("Invalid userId."); }

			if(order.getItemsNum() <= 0)
			{ throw new BusinessException("Invalid itemsNum."); }

			Timestamp currTime = new Timestamp(System.currentTimeMillis());
			order.setOrderTime(currTime);
		}

		orderService.batchSave(orders);
		return ResponseEntity.status(HttpStatus.OK).body(Result.success("success"));
	}
}