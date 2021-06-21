package com.singfung.blackfriday.controller;

import com.singfung.blackfriday.common.Result;
import com.singfung.blackfriday.model.Stock;
import com.singfung.blackfriday.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

	@PostMapping("")
	@ResponseBody
	public ResponseEntity<Result<Object>> add(@RequestBody @Validated(Stock.Insert.class) Stock stock)
	{
		stockService.save(stock);
		return ResponseEntity.status(HttpStatus.OK).body(Result.success());
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

	//Load Stock information to Redis
 	@PutMapping("sync-redis")
	@ResponseBody
	public ResponseEntity<Result<Object>> syncRedis()
	{
		stockService.syncRedis();
		return ResponseEntity.status(HttpStatus.OK).body(Result.success());
	}
}