package com.singfung.blackfriday.service.impl;

import com.singfung.blackfriday.dao.OrderDAO;
import com.singfung.blackfriday.model.Order;
import com.singfung.blackfriday.service.OrderService;
import com.singfung.blackfriday.service.StockRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService
{
//	@Autowired
//	private RedisTemplate redisTemplate = null;

	@Autowired
	private StockRedisService stockRedisService = null;

	@Autowired
	private OrderDAO orderDAO;

	// Lua script
	String script = "local listKey = 'order_list_'..KEYS[1] \n"
	              + "local stock = 'stock_'..KEYS[1] \n"
			      + "local stockNum = tonumber(redis.call('hget', stock, 'stockNum')) \n"
			      + "if stockNum <= 0 then return 0 end \n"
			      + "stockNum = stockNum -1 \n"
			      + "redis.call('hset', stock, 'stockNum', tostring(stockNum)) \n"
			      + "redis.call('rpush', listKey, ARGV[1]) \n"
			      + "if stockNum == 0 then return 2 end \n"
			      + "return 1 \n";

	// 在缓存LUA脚本后，使用该变量保存Redis返回的32位的SHA1编码，使用它去执行缓存的LUA脚本[加入这句话]
	String sha1 = null;

	@Override
	public Long generateAnOrderInRedis(int stockId, int userId)
	{
		// 当前抢红包用户和日期信息
		String args = userId + "-" + System.currentTimeMillis();
		Long result = null;
		// 获取底层Redis操作对象
//		Jedis jedis = (Jedis) redisTemplate.getConnectionFactory().getConnection().getNativeConnection();
		Jedis jedis = new Jedis("localhost",6379);
		try
		{
			// 如果脚本没有加载过，那么进行加载，这样就会返回一个sha1编码
			if (sha1 == null)
			{ sha1 = jedis.scriptLoad(script); }
			// 执行脚本，返回结果
			Object res = jedis.evalsha(sha1, 1, stockId + "", args);
			result = (Long) res;
			// 返回2时为最后一个红包，此时将抢红包信息通过异步保存到数据库中
			// The stockNum becomes 0 now, the orders will be saved into db
			if (result == 2)
			{ stockRedisService.saveOrdersFromRedisToDB(stockId); }
		}

		finally
		{
			if (jedis != null && jedis.isConnected())
			{ jedis.close(); }
		}

		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void batchSave(List<Order> orders)
	{
		orderDAO.batchInsert(orders);
	}
}
