package com.gpw.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
  *  服务端
 * @author Gpw
 *
 */
public class Server {
	/**
	 * 1.申请服务端口；
	 * 2.监听服务端口；
	 */ 
	private ServerSocket server;
	
	private List<PrintWriter> allout;//存放所有客户端输出流
	
	public Server() throws IOException {
		
		server = new ServerSocket(8088);
		
		allout = new ArrayList<PrintWriter>();
		
	}
	public void start() {
		try {
			while(true) {
			System.out.println("等待客户端连接");
			
			Socket socket = server.accept();//监听端口（阻塞方法）
			
			System.out.println("一个客户端连接！");
			/*
			 * 创建一个线程与该客户端交互
			 * */
			ClientHandler handler = new ClientHandler(socket);
			Thread t = new Thread(handler);
			t.start();
			}	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//将给定的消息广播到所有客户端
	private void sendMessage(String message) {
		synchronized (allout) {
			for(PrintWriter o:allout) {
				o.println(message);
			}
		}
	}
	public static void main(String[] args) {
		try {
			Server server = new Server() ;
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class ClientHandler implements Runnable{
		/**
		 * 当前线程通过socket与指定客户端进行交互
		 * */
		private Socket socket;
		
		private String host;//远程服务器地址，也就是客户端地址
		
		public ClientHandler(Socket socket) {
				this.socket=socket;
				InetAddress address = socket.getInetAddress();
				host = address.getHostAddress();
		}
		
		public void run() {
			PrintWriter pw = null;
			String name = null;
			try {
				
		InputStream in 
				= socket.getInputStream();
		InputStreamReader isr 
				= new InputStreamReader(in,"UTF-8");
		BufferedReader br = new BufferedReader(isr);
		   name = br.readLine();
		
		/**
		 *通过socket获取输出流，用于将数据发送到客户端 */
		OutputStream out 
				= socket.getOutputStream();
		OutputStreamWriter osw 
				= new OutputStreamWriter(out,"UTF-8");//返回字符串
		     pw = new PrintWriter(osw,true);//自动行刷新
		
		synchronized (allout) {//线程锁，因为各个客户端都在调用该集合的add方法
			allout.add(pw);//将消息放入list集合
		}
		
		
		sendMessage(name+"上线了！当前在线"+allout.size()+"人");
		
		String message =null;
		while((message = br.readLine())!=null) {
			//pw.println(host+"说："+message);	
			//转发给所有客户端
			
			sendMessage(name+"说："+message);
			
		}	
			
			} catch (Exception e) {
				//e.printStackTrace();
			}finally {
				//处理客户端下线后的工作
				//将该客户端的输出流从共享集合中删除
				
				synchronized (allout) {
					allout.remove(pw);
				}
				
				sendMessage(name+"下线了！");
				if(socket!=null) {}
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
