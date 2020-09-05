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
	// 每次取出1000条，避免一次取出消耗太多内存
	private static final int TIME_SIZE = 1000;

	@Autowired
	private RedisTemplate redisTemplate = null; // RedisTemplate
	@Autowired
	private StockService stockService = null;
	@Autowired
	private OrderService orderService = null;

	@Override
	// 开启新线程运行
	@Async
	public void saveOrdersFromRedisToDB(int stockId)
	{
		System.err.println("开始保存数据");
		Long start = System.currentTimeMillis();
		// 获取列表操作对象
		BoundListOperations ops = redisTemplate.boundListOps(PREFIX + stockId);
		Jedis jedis = new Jedis("localhost",6379);
		Long size = ops.size();
		Long times = size % TIME_SIZE == 0 ? size / TIME_SIZE : size / TIME_SIZE + 1;
		int count = 0;
		List<Order> orderList = new ArrayList<Order>(TIME_SIZE);
		
		for (int i = 0; i < times; i++) 
		{
			// 获取至多TIME_SIZE个抢红包信息
			List<String> userIdList = null;

			if(i == 0)
			{
//				userIdList = ops.range(i * TIME_SIZE, (i + 1) * TIME_SIZE);
				userIdList = jedis.lrange(PREFIX + stockId, i * TIME_SIZE, (i + 1) * TIME_SIZE);
			} else {
//				userIdList = ops.range(i * TIME_SIZE + 1, (i + 1) * TIME_SIZE);
				userIdList = jedis.lrange(PREFIX + stockId, i * TIME_SIZE + 1, (i + 1) * TIME_SIZE);
			}
			
			orderList.clear();
			
			// 保存红包信息
			for (int j = 0; j < userIdList.size(); j++) 
			{
				String args = userIdList.get(j).toString();
				String[] arr = args.split("-");
				String userIdStr = arr[0];
				String timeStr = arr[1];
				int userId = Integer.parseInt(userIdStr);
				Long time = Long.parseLong(timeStr);
				// 生成抢红包信息
				Order order = new Order();
				order.setProductId(stockId);
				order.setUserId(userId);
				order.setItemsNum(1);
				order.setOrderTime(new Timestamp(time));
				orderList.add(order);
			}
			// 插入抢红包信息
			count += executeBatch(orderList);
		}
		// 删除Redis列表
		redisTemplate.delete(PREFIX + stockId);
		Long end = System.currentTimeMillis();
		System.err.println("保存数据结束，耗时" + (end - start) + "毫秒，共" + count + "条记录被保存。");
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