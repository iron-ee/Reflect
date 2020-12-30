package com.cos.reflect.controller;

import com.cos.reflect.anno.RequestMapping;
import com.cos.reflect.controller.dto.JoinDto;
import com.cos.reflect.controller.dto.LoginDto;
import com.cos.reflect.model.User;

public class UserController {
	
	@RequestMapping("/user/join")
	public String join(JoinDto dto) {		// username, password, email
		System.out.println("join() 함수 호출 됨");
		System.out.println(dto);
		return "/";
	}
	
	@RequestMapping("/user/login")
	public String login(LoginDto dto) {		// username, password
		System.out.println("login() 함수 호출 됨");
		System.out.println(dto);
		return "/";
	}
	
	@RequestMapping("/user/list")
	public String list(User user) {		// username, password
		System.out.println("list() 함수 호출 됨");
		System.out.println(user);
		return "/";
	}
	
	@RequestMapping("/user")
	public String user() {
		System.out.println("user() 함수 호출 됨");
		return "/";
	}
	
	@RequestMapping("/hello")
	public String hello() {
		System.out.println("hello() 함수 호출 됨");
		return "/";
	}
}