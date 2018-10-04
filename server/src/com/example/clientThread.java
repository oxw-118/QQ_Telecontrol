package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/***
 * 该类用于与被控端单独交流 一个被控端对应一个线程
 * 唯一用处就是向客户端发送心跳包检测是否超时
 * @author Administrator
 *
 */

//子进程类
class clientThread implements Runnable
{
	clientInfo c;
	
	public clientThread(clientInfo c1)
	{
		c = c1;
	}
	
	public void run()
	{
		Demo.CQ.sendPrivateMsg(Demo.adminQQ,"新客户端已经加入子线程,ID: "+c.id);
		Demo.CQ.logInfo("clientThread", "新客户端已经加入子线程,ID: "+c.id);
		BufferedReader read;
		new Thread(new clientRecvThread(c)).start();
		
		try {
			read = new BufferedReader(new InputStreamReader(c.s.getInputStream(),"GBK"));
			String line;
			String heart = "Heart";	//心跳包内容
			OutputStream os= c.s.getOutputStream();
			
    		while(true)	//断开链接就退出循环
    		{
    			//Demo.CQ.sendPrivateMsg(Demo.adminQQ, "来自被控端[id:"+c.id+"]的消息:"+read.readLine());
    			try {
    				Thread.sleep(1000);
    				os.write(heart.getBytes("GBK"));
    				os.flush();	//不缓存信息 直接发送
				} catch (InterruptedException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}catch(IOException e1)
    			{
					Demo.CQ.sendPrivateMsg(Demo.adminQQ, "[id= "+ c.id+"] 下线");
					Demo.aClientQueue.remove(c);
					return ;
    			}
    		}	
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	    
	}
}