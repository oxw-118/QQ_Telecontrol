package com.example;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class s_logFile 
{
	public int errorCode = 0;
	private String LogFileName = "./log.log";
	private FileOutputStream out;
	
	s_logFile() //构造函数 无参默认
	{
		try {
			out = new FileOutputStream(LogFileName,true);
			
		} catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			errorCode = 1; //找不到文件
		}
	}
	
	s_logFile(String logFileName) //日志文件完整文件名
	{
		this.LogFileName = logFileName; 
		try 
		{
			out = new FileOutputStream(LogFileName,true);
			
		} catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			errorCode = 1; //找不到文件
		}
	}
	
	public void printLog(String log)
	{
		try 
		{
			out.write(log.getBytes());
			out.write("\r\n".getBytes());
		} 
		catch (IOException e) 
		{
			errorCode = -2; //文件IO异常
			e.printStackTrace();
		}
	}
	
	public static String getTime()
	{
		Date day=new Date();    
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		return df.format(day);
	}
}
