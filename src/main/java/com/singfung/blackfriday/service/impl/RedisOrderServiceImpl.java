package com.singfung.blackfriday.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.singfung.blackfriday.model.Order;
import com.singfung.blackfriday.service.OrderService;
import com.singfung.blackfriday.service.RedisOrderService;
import com.singfung.blackfriday.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RedisOrderServiceImpl implements RedisOrderService
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

	String lua_script = "local listKey = 'order_list_'..KEYS[1] \n"
			+ "local stock = 'stock_'..KEYS[1] \n"
			+ "local stockNum = tonumber(redis.call('hget', stock, 'stockNum')) \n"
			+ "if stockNum <= 0 then return 0 end \n"
			+ "stockNum = stockNum -1 \n"
			+ "redis.call('hset', stock, 'stockNum', tostring(stockNum)) \n"
			+ "redis.call('rpush', listKey, ARGV[1]) \n"
			+ "if stockNum == 0 then return 2 end \n"
			+ "return 1 \n";

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean generateAnOrderInRedis(int stockId, int userId)
	{
		stockService.findById(stockId);

		String args = userId + "-" + System.currentTimeMillis();
		Long result = null;

		DefaultRedisScript<Long> script = new DefaultRedisScript<>();
		script.setResultType(Long.class);
		script.setScriptText(lua_script);

		ArrayList<String> keys = new ArrayList<>();
		keys.add("" + stockId);

		Object res = redisTemplate.execute(script, keys, args);

		result = (Long) res;

		if (result == 2)
		{ saveOrdersFromRedisToDB(stockId); }

		return result > 0;
	}

	@Async
	@Transactional(rollbackFor = Exception.class)
	public void saveOrdersFromRedisToDB(int stockId)
	{
		System.err.println("Start saving orders from Redis into DB...");
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
				order.setOrderedTime(new Timestamp(time));
				order.setDbCreatedTime(new Timestamp(System.currentTimeMillis()));
				orderList.add(order);
			}

			count += executeBatch(orderList, stockId);
		}

		redisTemplate.delete(PREFIX + stockId);
		Long end = System.currentTimeMillis();
		System.err.println("End, costing " + (end - start) + " ms, with " + count + " records saved.");
	}

	@Transactional(rollbackFor = Exception.class)
	public int executeBatch(List<Order> orderList, int stockId)
	{
		stockService.decreaseStockNum(stockId, orderList.size());
		orderService.batchSave(orderList);

		return orderList.size();
	}
}