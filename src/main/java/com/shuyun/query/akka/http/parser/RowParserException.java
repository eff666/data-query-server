package com.shuyun.query.akka.http.parser;


public class RowParserException extends RuntimeException {

	public RowParserException(String msg) {
		super(msg);
	}
	
	public RowParserException(String msg, Throwable e) {
		super(msg, e);
	}

	private static final long serialVersionUID = 1L;
}
