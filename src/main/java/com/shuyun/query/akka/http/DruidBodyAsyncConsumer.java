package com.shuyun.query.akka.http;


import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.shuyun.query.meta.ReportPage;
import com.shuyun.query.meta.ReportResultForEs;
import com.shuyun.query.process.PostProcessorFactory;
import com.shuyun.query.process.QueryContext;
import org.apache.log4j.Logger;

import akka.actor.ActorRef;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.Bytes;

public class DruidBodyAsyncConsumer implements BodyConsumer {
	private static Logger logger = Logger.getLogger(DruidBodyAsyncConsumer.class);

	List<Byte> stream = new LinkedList<>();
	JsonFactory jsonFactory = new JsonFactory();
	ObjectMapper mapper = new ObjectMapper();
	boolean sent = false;

	ActorRef sender;
	ActorRef receiver;
	QueryContext queryContext;
	ByteStreamJsonParser rowparser;
	private List<Object> dataSet = new LinkedList<>();


	public DruidBodyAsyncConsumer(ActorRef sender, ActorRef receiver, QueryContext queryContext) {
		this.sender = sender;
		this.receiver = receiver;
		this.queryContext = queryContext;
		this.rowparser = new ByteStreamJsonParser(jsonFactory, mapper, queryContext.getTypeRef());
	}
	
	public void write(byte[] bytes) throws IOException {
		if(null == bytes){
			return;
		}
		stream.addAll(Bytes.asList(bytes));
	}

	public int tryParse() throws IOException {
		List<Object> ret = rowparser.tryParse(stream);
		stream.clear();
		for(Object row : ret){
			dataSet.add(row);
		}
		return ret.size();
	}

	public boolean trySend(boolean sendAll) {
		if (!sent) {
			if(sendAll){
				ReportResultForEs ret = PostProcessorFactory.create(queryContext).process(dataSet);
				ret.setPage(new ReportPage(dataSet.size(), rowparser.getTotal()));
				sender.tell(ret, receiver);
				sent = true;
			}
		}

		return sent;
	}

	public boolean tryClose() throws IOException {
		stream.clear();
		return true;
	}

	public boolean tryCache(){
		return false;
	}

	public boolean hasSent() {
		return sent;
	}
}

