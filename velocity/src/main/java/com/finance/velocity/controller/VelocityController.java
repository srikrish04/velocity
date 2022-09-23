package com.finance.velocity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finance.velocity.service.VelocityService;

@RestController
public class VelocityController {
    
	@Autowired
	private VelocityService velocityService;
	
	@GetMapping(path="/")
    public void run() 
    {
		this.velocityService.processTransaction("input.txt");
    }

}
