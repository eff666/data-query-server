package com.shuyun.query.akka.http;

import java.util.ArrayList;
import java.util.List;

import com.ning.http.client.*;
import org.apache.log4j.Logger;

import com.google.common.primitives.Bytes;

public class DruidAsyncHandler implements AsyncHandler<Boolean> {
	private boolean isSuccess = true;
	
	List<Byte> byteCache = new ArrayList<>();
	final static int trunkSize = 8192;

    @Override
    public void onThrowable(Throwable throwable) {

    }

    final BodyConsumer consumer;

    public DruidAsyncHandler(BodyConsumer consumer) {
        this.consumer = consumer;
    }

    public STATE onBodyPartReceived(final HttpResponseBodyPart content)
            throws Exception {

        byte[] bytes = content.getBodyPartBytes();
        for(byte b : bytes){
            byteCache.add(b);
        }

        if(byteCache.size() >= trunkSize){
            consumer.write(Bytes.toArray(byteCache));
            byteCache.clear();
            consumer.tryParse();
            consumer.trySend(false);
        }

        return STATE.CONTINUE;
    }

    public STATE onStatusReceived(final HttpResponseStatus status)
            throws Exception {
        // 通过端口号进行判断
        logger.debug("the status from es is " + status.getStatusCode());
        if(status.getStatusCode() != 200) {
            this.isSuccess = false;
            throw new RuntimeException("some error may occur from es ..........");
        }
        return STATE.CONTINUE;
    }

    public STATE onHeadersReceived(final HttpResponseHeaders headers)
            throws Exception {
        return STATE.CONTINUE;
    }

    public Boolean onCompleted() throws Exception {
        if(isSuccess){
            if(byteCache.size() > 0){
                consumer.write(Bytes.toArray(byteCache));
                byteCache.clear();
                consumer.tryParse();
            }

            consumer.trySend(true);
            consumer.tryClose();
            return true;
        }
        return false;
	}

    private final static Logger logger = Logger.getLogger(DruidAsyncHandler.class);
}

