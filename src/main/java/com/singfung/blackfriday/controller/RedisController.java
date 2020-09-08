package com.singfung.blackfriday.controller;

import com.singfung.blackfriday.common.Result;
import com.singfung.blackfriday.model.Stock;
import com.singfung.blackfriday.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/blackfriday/redis")
public class RedisController
{
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StockService stockService = null;

    @PostMapping("")
    @ResponseBody
    public ResponseEntity<Result<Object>> test()
    {
        Stock stock = stockService.findById(1);

        redisTemplate.opsForValue().set("test", stock.toString());

        return ResponseEntity.status(HttpStatus.OK).body(Result.success("success"));
    }
}
