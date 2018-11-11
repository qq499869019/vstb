package com.um.push.drv;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpDriver extends UmDriver {
	
	private DatagramSocket m_udpsocket;
	private InetAddress m_udpIp;
    private int m_udptPort = 8899;
	public int callback(int eventid) {
		return 0;
	}

	@Override
	public boolean open(int bits) {
		try {
			m_udpsocket = new DatagramSocket();
			m_udpIp = InetAddress.getByName("127.0.0.1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public int close() {
		if(m_udpsocket != null)
			m_udpsocket.close();
		return 0;
	}

	@Override
	public int write(byte[] data) {
		
		try {
			m_udpsocket.send(new DatagramPacket(data, data.length,  m_udpIp, m_udptPort));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int write(byte[] data, int offset, int len) {
		try {
			m_udpsocket.send(new DatagramPacket(data, offset, len,  m_udpIp, m_udptPort));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
