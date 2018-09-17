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
	
	s_logFile() //���캯�� �޲�Ĭ��
	{
		try {
			out = new FileOutputStream(LogFileName,true);
			
		} catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			errorCode = 1; //�Ҳ����ļ�
		}
	}
	
	s_logFile(String logFileName) //��־�ļ������ļ���
	{
		this.LogFileName = logFileName; 
		try 
		{
			out = new FileOutputStream(LogFileName,true);
			
		} catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			errorCode = 1; //�Ҳ����ļ�
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
			errorCode = -2; //�ļ�IO�쳣
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
