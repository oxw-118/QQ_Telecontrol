package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/***
 * ���������뱻�ض˵������� һ�����ض˶�Ӧһ���߳�
 * Ψһ�ô�������ͻ��˷�������������Ƿ�ʱ
 * @author Administrator
 *
 */

//�ӽ�����
class clientThread implements Runnable
{
	clientInfo c;
	
	public clientThread(clientInfo c1)
	{
		c = c1;
	}
	
	public void run()
	{
		Demo.CQ.sendPrivateMsg(Demo.adminQQ,"�¿ͻ����Ѿ��������߳�,ID: "+c.id);
		Demo.CQ.logInfo("clientThread", "�¿ͻ����Ѿ��������߳�,ID: "+c.id);
		BufferedReader read;
		new Thread(new clientRecvThread(c)).start();
		
		try {
			read = new BufferedReader(new InputStreamReader(c.s.getInputStream(),"GBK"));
			String line;
			String heart = "Heart";	//����������
			OutputStream os= c.s.getOutputStream();
			
    		while(true)	//�Ͽ����Ӿ��˳�ѭ��
    		{
    			//Demo.CQ.sendPrivateMsg(Demo.adminQQ, "���Ա��ض�[id:"+c.id+"]����Ϣ:"+read.readLine());
    			try {
    				Thread.sleep(1000);
    				os.write(heart.getBytes("GBK"));
    				os.flush();	//��������Ϣ ֱ�ӷ���
				} catch (InterruptedException e) {
					// TODO �Զ����ɵ� catch ��
					e.printStackTrace();
				}catch(IOException e1)
    			{
					Demo.CQ.sendPrivateMsg(Demo.adminQQ, "[id= "+ c.id+"] ����");
					Demo.aClientQueue.remove(c);
					return ;
    			}
    		}	
		} catch (UnsupportedEncodingException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
	    
	}
}