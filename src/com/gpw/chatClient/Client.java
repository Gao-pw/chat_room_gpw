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
 * �ͻ���
 * @author Gpw
 *
 */
public class Client {
	private Socket socket;	
	/**
	 *���췽��
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */	
	public Client() throws UnknownHostException, IOException {
		System.out.println("�������������ӡ���");
		socket = new Socket("154.92.18.193",8088);//������IP��ַ������˿ں�
		System.out.println("���ӳɹ�");
	}
	/***
	 * ����������������
	 */
	public void start() {
		try {
		   Scanner scan = new Scanner(System.in);
		   System.out.println("����һ���ǳƣ�");
		   String name = scan.nextLine();
		   
		   OutputStream out 
		   		= socket.getOutputStream();
		   OutputStreamWriter osw 
		   		= new OutputStreamWriter(out,"UTF-8");
		   PrintWriter pw 
		   		= new PrintWriter(osw,true);//�Զ���ˢ��
		   pw.println(name);
		   ServerHandler handler = new ServerHandler();
		   Thread t = new Thread(handler);
		   t.start();//��ȡ�߳�
		   
		   String line = null;
		   System.out.println("Chat Starting!");
		   
		   while(true) {
			   line = scan.nextLine();
			   pw.println(line);//������		   
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
			System.out.println("�ͻ�������ʧ��");
		}		
	}
	/**
	 * �ͻ��˽������Է���˵���Ϣ
	 * @author Gpw
	 *
	 */	
	private class ServerHandler implements Runnable{
		public void run() {
			try {
				System.out.println("�����̳߳ɹ�");
				
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
