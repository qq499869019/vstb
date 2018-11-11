package com.um.push.drv;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class TcpDriver extends UmDriver {

	private ServerSocket m_server;
	private int m_tcptPort = 7788;
	private boolean m_run = false;
	private ArrayList<Socket> m_clientList = new ArrayList<Socket>();
	public int callback(int eventid) {
		return 0;
	}
	
	public void listen()
	{
		while(m_run)
		{
			try {
				Socket s =m_server.accept();
				m_clientList.add(s);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		clearAllClient();
	}
	private void clearAllClient() {
		while(m_clientList.size() > 0)
		{
			Socket s = m_clientList.get(0);
			m_clientList.remove(s);
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean open(int bits) {
		try {
			m_server = new ServerSocket(m_tcptPort);
			new Thread(new Runnable() {				
				public void run() {
					m_run = true;
					listen();
				}
			}).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public int close() {
		try {
			m_run = false;
			if(m_server != null)
				m_server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int write(byte[] data) {
		for(int i= 0; i < m_clientList.size(); i++)
		{
			Socket s = m_clientList.get(i);
			try {
				s.getOutputStream().write(data);
			} catch (IOException e) {
				try {
					s.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				m_clientList.remove(s);
			}
		}
		return 0;
	}

	@Override
	public int write(byte[] data, int offset, int len) {
		for(int i= 0; i < m_clientList.size(); i++)
		{
			Socket s = m_clientList.get(i);
			try {
				s.getOutputStream().write(data, offset, len);
			} catch (IOException e) {
				try {
					s.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				m_clientList.remove(s);
			}
		}
		return 0;
	}

}
