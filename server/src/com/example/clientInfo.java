package com.example;

import java.net.Socket;

/***
 * �������ڴ��汻�ض��û���Ϣ
 * @author Administrator
 *
 */

class clientInfo
{
	int id;
	Socket s;
	
	clientInfo(int i1,Socket s1)
	{
		id = i1;
		s = s1;
	}
}