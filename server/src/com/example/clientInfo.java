package com.example;

import java.net.Socket;

/***
 * 该类用于储存被控端用户信息
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