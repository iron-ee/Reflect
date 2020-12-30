package com.cos.reflect.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cos.reflect.anno.RequestMapping;
import com.cos.reflect.controller.UserController;


// 분기 시키기
public class Dispatcher implements Filter{

	private boolean isMatching = false;
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		// /user 파싱하기
		String endPoint = request.getRequestURI().replaceAll(request.getContextPath(),"");
		System.out.println("엔드포인트 : " + endPoint);
		
		UserController userController = new UserController();

		
		Method[] methods = userController.getClass().getDeclaredMethods();	// 그 파일에 메서드만 !!

		for (Method method : methods) {
			Annotation annotation = method.getDeclaredAnnotation(RequestMapping.class);
			RequestMapping requestMapping = (RequestMapping)annotation;
		
			if (requestMapping.value().equals(endPoint)) {
				isMatching = true;
				try {
					Parameter[] params = method.getParameters();	// login(LoginDto dto)
					String path = null;
					if (params.length != 0) {
						// 해당 dtoInstance를 리플렉션해서 set함수 호출(username, password)
						Object dtoInstance = params[0].getType().newInstance();
						setData(dtoInstance, request);
						path = (String) method.invoke(userController, dtoInstance);
					}else {
						path = (String) method.invoke(userController);
					}
					RequestDispatcher dis = request.getRequestDispatcher(path);	// 필터를 다시 안 탐!!
					dis.forward(request, response);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}
		if (isMatching == false) {
			response.setContentType("text/html; charset=utf-8");
			PrintWriter out = response.getWriter();
			out.println("잘못 된 주소 요청입니다. 404 error !");
			out.flush();
		}
	}
	
	private <T> void setData(T instance, HttpServletRequest request) {
		Enumeration<String> keys = request.getParameterNames();		// 크기 : 2 (username, password)
		while (keys.hasMoreElements()) { 	// 크기 만큼 돈다 -> 크기 2 (username, password)면 2번 돈다.
			String key = (String) keys.nextElement();
			String methodKey = keyToMethodKey(key);	//setUsername
			
			Method[] methods = instance.getClass().getDeclaredMethods();	// 5개
			
			for(Method method : methods) {
				if(method.getName().equals(methodKey)) {
					try {
						method.invoke(instance, request.getParameter(key));	//String 
					} catch (Exception e) {
						try {
							int value = Integer.parseInt(request.getParameter(key));
							method.invoke(instance, value);
						} catch (Exception e2) {
							e2.printStackTrace();
						}
						System.out.println("신경 쓸 필요없는 int 파싱 문제");
					}
				}
			}
		}
	}
	
	private String keyToMethodKey(String key) {
		String firstKey = "set";
		String upperKey = key.substring(0,1).toUpperCase();
		String remainKey = key.substring(1);
		
		return firstKey + upperKey + remainKey;
	}
}