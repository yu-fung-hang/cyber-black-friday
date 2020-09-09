package com.singfung.blackfriday.controller;

import com.singfung.blackfriday.common.Result;
import com.singfung.blackfriday.exception.BusinessException;
import com.singfung.blackfriday.model.Stock;
import com.singfung.blackfriday.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/blackfriday/stock")
public class StockController
{
	@Autowired
	private StockService stockService = null;
	@Autowired
	private RedisTemplate redisTemplate;

	@PostMapping("")
	@ResponseBody
	public ResponseEntity<Result<Object>> add(@RequestBody @Validated(Stock.Insert.class) Stock stock)
	{
		if (stock.getStockNum() != stock.getTotalNum())
		{ throw new BusinessException("totalNum should be equal to stockNum."); }

		uniqueName(stock.getName());

		stockService.save(stock);

		toRedis();

		return ResponseEntity.status(HttpStatus.OK).body(Result.success("success"));
	}

	//Increase the totalNum and stockNum
	@PutMapping("")
	@ResponseBody
	public ResponseEntity<Result<Object>> update(@RequestBody @Validated(Stock.Update.class) Stock stock)
	{
		Stock dbStock = stockService.findById(stock.getId());

		int increment = stock.getIncrement();

		dbStock.setTotalNum(dbStock.getTotalNum() + increment);
		dbStock.setStockNum(dbStock.getStockNum() + increment);

		String name = stock.getName();
		if(name != null)
		{ uniqueName(name); }

		dbStock.setName(name);

		stockService.update(dbStock);

		toRedis();

		return ResponseEntity.status(HttpStatus.OK).body(Result.success("success"));
	}

	@GetMapping("/{id}")
	@ResponseBody
	public Stock findById(@PathVariable int id)
	{
		return stockService.findById(id);
	}

	@GetMapping("")
	@ResponseBody
	public List<Stock> findAll()
	{
		return stockService.findAll();
	}

	public void uniqueName(String name)
	{
		List<Stock> stock_list = stockService.findAll();
		for(Stock stock : stock_list)
		{
			if(stock.getName().equals(name))
			{ throw new BusinessException("Conflict : This product is already in db, operation failed."); }
		}
	}

	//Load Stock information to Redis
 	@PutMapping("to-redis")
	@ResponseBody
	public ResponseEntity<Result<Object>> toRedis()
	{
		List<Stock> stock_list = stockService.findAll();

		for(Stock stock : stock_list)
		{
			int stockId = stock.getId();
			String key = "stock_" + stockId;
			String field = "stockNum";
			String value = "" + stock.getStockNum();
			redisTemplate.opsForHash().put(key, field, value);
		}

		return ResponseEntity.status(HttpStatus.OK).body(Result.success("success"));
	}
}