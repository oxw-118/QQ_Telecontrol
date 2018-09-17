package com.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class clientRecvThread implements Runnable
{
	clientInfo c;
	
	public clientRecvThread(clientInfo c1)
	{
		c = c1;
	}
	
	public void run()
	{
		Demo.CQ.logInfo("clentRecvThread", "进入接受信息子线程"+c.id);
		try {
			Demo.CQ.logInfo("clentRecvThread", "1");
			Thread.sleep(1000);
			InputStream read = c.s.getInputStream();
			Demo.CQ.logInfo("clentRecvThread", "2");
    		while(true)	//断开链接就退出循环
    		{
    			//Demo.CQ.sendPrivateMsg(Demo.adminQQ, "来自被控端[id:"+c.id+"]的消息:"+read.readLine());
    			Demo.CQ.logInfo("clentRecvThread", "进入循环");
    			try {
    				byte str[] = new byte[1024*10];
				    read.read(str);
				    Demo.CQ.logInfo("clientRecvThread", new String(str,"GBK"));
					Demo.CQ.sendPrivateMsg(Demo.adminQQ, new String(str,"GBK"));
				}catch(IOException e1)
    			{
					Demo.CQ.logInfo("clentRecvThread", "断开连接 异常");
					return ;
    			}
    		}	
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			//e.printStackTrace();
			Demo.CQ.logInfo("clientRecvThread", e.toString());
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			//e.printStackTrace();
			Demo.CQ.logInfo("clientRecvThread", e.toString());
		} catch (InterruptedException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	    
	}
}