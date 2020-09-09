package com.singfung.blackfriday.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.singfung.blackfriday.model.Order;
import com.singfung.blackfriday.service.OrderService;
import com.singfung.blackfriday.service.StockRedisService;
import com.singfung.blackfriday.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class StockRedisServiceImpl implements StockRedisService
{
	private static final String PREFIX = "order_list_";
	// Load 1000 records every time
	private static final int TIME_SIZE = 1000;

	@Autowired
	private RedisTemplate redisTemplate = null;
	@Autowired
	private StockService stockService = null;
	@Autowired
	private OrderService orderService = null;

	@Override
	@Async
	public void saveOrdersFromRedisToDB(int stockId)
	{
		System.err.println("Start saving the orders into DB...");
		Long start = System.currentTimeMillis();

		BoundListOperations ops = redisTemplate.boundListOps(PREFIX + stockId);
//		Jedis jedis = new Jedis("localhost",6379);
		Long size = ops.size();
		Long times = size % TIME_SIZE == 0 ? size / TIME_SIZE : size / TIME_SIZE + 1;
		int count = 0;
		List<Order> orderList = new ArrayList<Order>(TIME_SIZE);
		
		for (int i = 0; i < times; i++) 
		{
			List<String> userIdList = null;

			if(i == 0)
			{
//				userIdList = jedis.lrange(PREFIX + stockId, i * TIME_SIZE, (i + 1) * TIME_SIZE);
				userIdList = redisTemplate.opsForList().range(PREFIX + stockId, i * TIME_SIZE, (i + 1) * TIME_SIZE);
			} else {
//				userIdList = jedis.lrange(PREFIX + stockId, i * TIME_SIZE + 1, (i + 1) * TIME_SIZE);
				userIdList = redisTemplate.opsForList().range(PREFIX + stockId, i * TIME_SIZE + 1, (i + 1) * TIME_SIZE);
			}
			
			orderList.clear();

			for (int j = 0; j < userIdList.size(); j++) 
			{
				String args = userIdList.get(j).toString();
				String[] arr = args.split("-");
				String userIdStr = arr[0];
				String timeStr = arr[1];
				int userId = Integer.parseInt(userIdStr);
				Long time = Long.parseLong(timeStr);

				Order order = new Order();
				order.setProductId(stockId);
				order.setUserId(userId);
				order.setItemsNum(1);
				order.setOrderTime(new Timestamp(time));
				orderList.add(order);
			}

			count += executeBatch(orderList);
		}

		redisTemplate.delete(PREFIX + stockId);
		Long end = System.currentTimeMillis();
		System.err.println("End, costing " + (end - start) + " ms, with " + count + " records saved.");
	}

	private int executeBatch(List<Order> orderList)
	{
		HashMap<Integer, LinkedList<Order>> map = new HashMap<>();

		//Divide the orders based on stockId
		for(Order order : orderList)
		{
			int productId = order.getProductId();

			if(map.get(productId) == null)
			{
				LinkedList<Order> linkedList = new LinkedList<>();
				linkedList.add(order);
				map.put(productId, linkedList);
			} else {
				map.get(productId).add(order);
			}
		}

		for(int stockId : map.keySet())
		{
			LinkedList<Order> currList = map.get(stockId);
			int decrement = currList.size();
			stockService.decreaseStockNum(stockId, decrement);
			orderService.batchSave(currList);
		}

		return orderList.size();
	}
}