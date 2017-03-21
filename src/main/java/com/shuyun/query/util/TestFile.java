/*
package com.shuyun.query.util;

import org.elasticsearch.index.query.FilterBuilders;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.concurrent.ConcurrentLinkedQueue;

*/
/*

public class TestFile {

    public static void main(String[] agrs){
        long startTime = System.nanoTime();
        try{
            ByteBuffer bytebuffer = ByteBuffer.allocate(8 * 1024);
            FileInputStream fileInputStream = new FileInputStream("g:/realquery.log");
            FileOutputStream fileOutputStream = new FileOutputStream("g:/bb.txt");
            BufferedOutputStream outputStream = new BufferedOutputStream(fileOutputStream);
            FileChannel fileChannel_from = fileInputStream.getChannel();
//            FileChannel fileChannel_to = fileOutputStream.getChannel();
            int bytesCount = 0;
            while ((bytesCount = fileChannel_from.read(bytebuffer)) > 0) {
                //flip the buffer which set the limit to current position, and position to 0
                bytebuffer.flip();
                //write data from ByteBuffer to file
                outputStream.write(bytebuffer.array(), 0, bytesCount);
//                fileChannel_to.write(bytebuffer);
                //for the next read
                bytebuffer.clear();
            }
            outputStream.flush();
            outputStream.close();
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        System.out.println(System.nanoTime() - startTime);
    }

}*/
