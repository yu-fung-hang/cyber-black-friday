package com.singfung.blackfriday.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.singfung.blackfriday.common.Result;
import com.singfung.blackfriday.model.Order;
import com.singfung.blackfriday.model.Stock;
import com.singfung.blackfriday.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/blackfriday")
public class OrderController
{
	@Autowired
	private OrderService orderService = null;

	@PutMapping(value = "/redis-order")
	public Map<String, Object> generateOrdersWithRedis(int stockId, int userId)
	{
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long result = orderService.generateAnOrderInRedis(stockId, userId);
		boolean flag = result > 0;
		resultMap.put("result", flag);
		resultMap.put("message", flag ? "抢红包成功": "抢红包失败");
		return resultMap;
	}

	@PostMapping(value = "/orders")
	@ResponseBody
	public ResponseEntity<Result<Object>> add(@RequestBody List<Order> orders)
	{
		orderService.batchSave(orders);
		return ResponseEntity.status(HttpStatus.OK).body(Result.success("success"));
	}
}
