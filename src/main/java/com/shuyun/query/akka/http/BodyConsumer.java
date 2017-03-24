package com.shuyun.query.akka.http;

import java.io.IOException;

/**
 * Created by xxx on 5/9/15.
 */
public interface BodyConsumer {

    public void write(byte[] bytes) throws IOException;

    public int tryParse() throws IOException;

    public boolean trySend(boolean sendAll);

    public boolean tryCache();

    public boolean tryClose() throws IOException;

    public boolean hasSent();
}
