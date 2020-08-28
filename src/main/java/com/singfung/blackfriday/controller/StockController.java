package com.singfung.blackfriday.controller;

import com.singfung.blackfriday.common.Result;
import com.singfung.blackfriday.model.Stock;
import com.singfung.blackfriday.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/blackfriday")
public class StockController
{
	@Autowired
	private StockService stockService = null;

	@PostMapping(value = "/stock")
	@ResponseBody
	public ResponseEntity<Result<Object>> add(@RequestBody Stock stock)
	{
		stockService.save(stock);
		return ResponseEntity.status(HttpStatus.OK).body(Result.success("success"));
	}
}
