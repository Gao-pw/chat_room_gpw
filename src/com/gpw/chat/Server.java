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
  *  �����
 * @author Gpw
 *
 */
public class Server {
	/**
	 * 1.�������˿ڣ�
	 * 2.��������˿ڣ�
	 */ 
	private ServerSocket server;
	
	private List<PrintWriter> allout;//������пͻ��������
	
	public Server() throws IOException {
		
		server = new ServerSocket(8088);
		
		allout = new ArrayList<PrintWriter>();
		
	}
	public void start() {
		try {
			while(true) {
			System.out.println("�ȴ��ͻ�������");
			
			Socket socket = server.accept();//�����˿ڣ�����������
			
			System.out.println("һ���ͻ������ӣ�");
			/*
			 * ����һ���߳���ÿͻ��˽���
			 * */
			ClientHandler handler = new ClientHandler(socket);
			Thread t = new Thread(handler);
			t.start();
			}	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//����������Ϣ�㲥�����пͻ���
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
		 * ��ǰ�߳�ͨ��socket��ָ���ͻ��˽��н���
		 * */
		private Socket socket;
		
		private String host;//Զ�̷�������ַ��Ҳ���ǿͻ��˵�ַ
		
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
		 *ͨ��socket��ȡ����������ڽ����ݷ��͵��ͻ��� */
		OutputStream out 
				= socket.getOutputStream();
		OutputStreamWriter osw 
				= new OutputStreamWriter(out,"UTF-8");//�����ַ���
		     pw = new PrintWriter(osw,true);//�Զ���ˢ��
		
		synchronized (allout) {//�߳�������Ϊ�����ͻ��˶��ڵ��øü��ϵ�add����
			allout.add(pw);//����Ϣ����list����
		}
		
		
		sendMessage(name+"�����ˣ���ǰ����"+allout.size()+"��");
		
		String message =null;
		while((message = br.readLine())!=null) {
			//pw.println(host+"˵��"+message);	
			//ת�������пͻ���
			
			sendMessage(name+"˵��"+message);
			
		}	
			
			} catch (Exception e) {
				//e.printStackTrace();
			}finally {
				//����ͻ������ߺ�Ĺ���
				//���ÿͻ��˵�������ӹ�������ɾ��
				
				synchronized (allout) {
					allout.remove(pw);
				}
				
				sendMessage(name+"�����ˣ�");
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
