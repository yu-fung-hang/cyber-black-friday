package com.singfung.blackfriday.service.impl;

import com.singfung.blackfriday.dao.OrderDAO;
import com.singfung.blackfriday.model.Order;
import com.singfung.blackfriday.service.OrderService;
import com.singfung.blackfriday.service.StockRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService
{
	@Autowired
	private RedisTemplate redisTemplate = null;

	@Autowired
	private StockRedisService stockRedisService = null;

	@Autowired
	private OrderDAO orderDAO;

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
	public Long generateAnOrderInRedis(int stockId, int userId)
	{
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
		{ stockRedisService.saveOrdersFromRedisToDB(stockId); }

		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void batchSave(List<Order> orders)
	{
		orderDAO.batchInsert(orders);
	}
}