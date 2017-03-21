package com.shuyun.query.queue;

import org.apache.log4j.Logger;

public class QueryExecuteThread implements Runnable {
    private static Logger logger = Logger.getLogger(QueryExecuteThread.class);

    private Object query;
    private int delay;

    static {

    }

    public QueryExecuteThread() {
    }

    public QueryExecuteThread(Object query, int delay) {
        super();
        this.query = query;
        this.delay = delay;
    }

    public void run() {

    }


}
