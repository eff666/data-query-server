package com.shuyun.query.queue;

import org.apache.log4j.Logger;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class QueryQueue {
    private static Logger logger = Logger.getLogger(QueryQueue.class);

    public static ArrayBlockingQueue<Object> queue = null;
    private static AtomicInteger count = new AtomicInteger();
    static {
        queue = new ArrayBlockingQueue<Object>(10);
    }

    private static Object queryEntry = null;

    public QueryQueue() {
    }

    public QueryQueue(Object queryEntry) {
        this.queryEntry = queryEntry;
    }

    public static void addQuery() throws UnsupportedEncodingException {

    }

}
