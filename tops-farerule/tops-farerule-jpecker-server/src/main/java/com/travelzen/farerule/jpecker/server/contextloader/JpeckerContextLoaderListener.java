package com.travelzen.farerule.jpecker.server.contextloader;

import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;

import com.travelzen.farerule.jpecker.server.JpeckerServiceServer;

public class JpeckerContextLoaderListener extends ContextLoaderListener{
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		new Thread() {
			public void run() {
				JpeckerServiceServer.start();
			}
		}.start();
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		JpeckerServiceServer.stop();
		super.contextDestroyed(event);
	}
}
