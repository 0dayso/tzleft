package com.travelzen.farerule.jpecker.server.test;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import com.travelzen.farerule.jpecker.server.JpeckerService;
import com.travelzen.farerule.jpecker.server.JpeckerServiceHandler;

public class JpeckerServer {
	
	public static JpeckerServiceHandler handler;
	public static JpeckerService.Processor<JpeckerServiceHandler> processor;
	
	public static void start(JpeckerService.Processor<JpeckerServiceHandler> processor) {
		try {
			TServerTransport serverTransport = new TServerSocket(9135);
			TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));
			
			// Use this for a multithreaded server 
			// TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor)); 
			
			System.out.println("Jpecker Server starting...");
			server.serve();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			handler = new JpeckerServiceHandler();
			processor = new JpeckerService.Processor<JpeckerServiceHandler>(handler);
			JpeckerServer.start(processor);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
