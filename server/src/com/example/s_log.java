package com.example;

public class s_log {
	public static void main(String[] args) 
	{
		s_logFile s1 = new s_logFile("233.log");
		s1.printLog("\r\n日志1\r\n");
		System.out.println(s1.getTime());
	}
}
