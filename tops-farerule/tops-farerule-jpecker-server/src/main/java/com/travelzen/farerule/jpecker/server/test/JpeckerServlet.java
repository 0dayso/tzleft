package com.travelzen.farerule.jpecker.server.test;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TServlet;

import com.travelzen.farerule.jpecker.server.JpeckerService;
import com.travelzen.farerule.jpecker.server.JpeckerServiceHandler;

public class JpeckerServlet extends TServlet {

	private static final long serialVersionUID = 1L;

	public JpeckerServlet() {
		super(
			new JpeckerService.Processor<JpeckerServiceHandler>(
					new JpeckerServiceHandler()), 
					new TCompactProtocol.Factory()
		);
	}

}
