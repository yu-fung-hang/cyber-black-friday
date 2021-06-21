package com.singfung.blackfriday.controller;

import com.singfung.blackfriday.common.Result;
import com.singfung.blackfriday.service.ResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/blackfriday")
public class ResetController
{
    @Autowired
    private ResetService resetService = null;

    @DeleteMapping
    public ResponseEntity<Result<Object>> reset()
    {
        resetService.reset();
        return ResponseEntity.status(HttpStatus.OK).body(Result.success());
    }
}
