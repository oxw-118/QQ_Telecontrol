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
		Demo.CQ.logInfo("clentRecvThread", "���������Ϣ���߳�"+c.id);
		try {
			Demo.CQ.logInfo("clentRecvThread", "1");
			Thread.sleep(1000);
			InputStream read = c.s.getInputStream();
			Demo.CQ.logInfo("clentRecvThread", "2");
    		while(true)	//�Ͽ����Ӿ��˳�ѭ��
    		{
    			//Demo.CQ.sendPrivateMsg(Demo.adminQQ, "���Ա��ض�[id:"+c.id+"]����Ϣ:"+read.readLine());
    			Demo.CQ.logInfo("clentRecvThread", "����ѭ��");
    			try {
    				byte str[] = new byte[1024*10];
				    read.read(str);
				    Demo.CQ.logInfo("clientRecvThread", new String(str,"GBK"));
					Demo.CQ.sendPrivateMsg(Demo.adminQQ, new String(str,"GBK"));
				}catch(IOException e1)
    			{
					Demo.CQ.logInfo("clentRecvThread", "�Ͽ����� �쳣");
					return ;
    			}
    		}	
		} catch (UnsupportedEncodingException e) {
			// TODO �Զ����ɵ� catch ��
			//e.printStackTrace();
			Demo.CQ.logInfo("clientRecvThread", e.toString());
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			//e.printStackTrace();
			Demo.CQ.logInfo("clientRecvThread", e.toString());
		} catch (InterruptedException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
	    
	}
}