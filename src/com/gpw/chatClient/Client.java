package com.gpw.chatClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * 客户端
 * @author Gpw
 *
 */
public class Client {
	private Socket socket;	
	/**
	 *构造方法
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */	
	public Client() throws UnknownHostException, IOException {
		System.out.println("正在与服务端连接。。");
		socket = new Socket("154.92.18.193",8088);//服务器IP地址，服务端口号
		System.out.println("连接成功");
	}
	/***
	 * 启动方法（交互）
	 */
	public void start() {
		try {
		   Scanner scan = new Scanner(System.in);
		   System.out.println("输入一个昵称：");
		   String name = scan.nextLine();
		   
		   OutputStream out 
		   		= socket.getOutputStream();
		   OutputStreamWriter osw 
		   		= new OutputStreamWriter(out,"UTF-8");
		   PrintWriter pw 
		   		= new PrintWriter(osw,true);//自动行刷新
		   pw.println(name);
		   ServerHandler handler = new ServerHandler();
		   Thread t = new Thread(handler);
		   t.start();//读取线程
		   
		   String line = null;
		   System.out.println("Chat Starting!");
		   
		   while(true) {
			   line = scan.nextLine();
			   pw.println(line);//缓冲流		   
		   }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		
		try {
			Client client = new Client();
			client.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("客户端启动失败");
		}		
	}
	/**
	 * 客户端接受来自服务端的消息
	 * @author Gpw
	 *
	 */	
	private class ServerHandler implements Runnable{
		public void run() {
			try {
				System.out.println("创建线程成功");
				
				InputStream in = socket.getInputStream();
				InputStreamReader isr
					= new InputStreamReader(in,"UTF-8");
				BufferedReader br = new BufferedReader(isr);
				
				String message = null;
				
				while((message=br.readLine())!=null) {
					System.out.println(message);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
