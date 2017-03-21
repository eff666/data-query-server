package com.shuyun.query.jersey.context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.io.Closeables;
import com.shuyun.query.meta.DownloadFileConf;
import com.shuyun.query.meta.FileDownload;
import com.shuyun.query.meta.FileType;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


/**
 * Created by xxx on 2016/1/11.
 */
public class FileUploadService extends HttpServlet {

    private static Logger logger = Logger.getLogger(UpdateService.class);
    private static final String ERROR_CONTENT_TYPE = "application/json;charset=UTF-8";

    //Process the HTTP Get request
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        //get request content and parser
        String requestContent = request.getParameter("param");
        if(Strings.isNullOrEmpty(requestContent)){
            // 抛错
            response.setContentType(ERROR_CONTENT_TYPE);
            try{
                PrintWriter printWriter = response.getWriter();
                printWriter.print("{\"message\":\"param must not be blank\"}");
                printWriter.flush();
                printWriter.close();
            }
            catch(IOException e){
                logger.error("param is blank");
            }
            return;
        }
        logger.debug("filedownload request is " + requestContent);

        FileDownload fileDownload = null;
        JsonNode rootNode = null;
        try {
            fileDownload = new ObjectMapper().readValue(requestContent, FileDownload.class);
            rootNode = new ObjectMapper().readTree(requestContent);
        } catch(Exception e){
            response.setContentType(ERROR_CONTENT_TYPE);
            try{
                PrintWriter printWriter = response.getWriter();
                printWriter.print("{\"message\":\"parser param request error....\"}");
                printWriter.flush();
                printWriter.close();
            }
            catch(IOException ioe){
                logger.error("parser param request error");
            }
            return;
        }

        // verificat用户的权限
        if(!("mx".equalsIgnoreCase(fileDownload.getVerificate().getKey()) && "123".equalsIgnoreCase(fileDownload.getVerificate().getValue()))){
            response.setContentType(ERROR_CONTENT_TYPE);
            try{
                PrintWriter printWriter = response.getWriter();
                printWriter.print("{\"message\":\"verificate error\"}");
                printWriter.flush();
                printWriter.close();
            }
            catch(IOException e){
                logger.error("verificate error");
            }
            return;
        }

        //校验内容
        /*if(Strings.isNullOrEmpty(fileDownload.getContent().getType()) || Strings.isNullOrEmpty(fileDownload.getContent().getShop_id())
                || Strings.isNullOrEmpty(fileDownload.getContent().getTime())){

        }*/
        try{
            FileType.valueOf(fileDownload.getContent().getType());
        }
        catch(Exception exception){
            try{
                PrintWriter printWriter = response.getWriter();
                printWriter.print("{\"message\":\"do not exists this type\"}");
                printWriter.flush();
                printWriter.close();
            }
            catch(IOException e){
                logger.error("do not exists this type");
            }
            return;
        }
        //根据content内容获取文件名,文件写入的时候lock
        String path = DownloadFileConf.getInstance().getPath() + "/" + fileDownload.getContent().getType() + "/" +
                fileDownload.getContent().getShop_id() + "/" +
                fileDownload.getContent().getTime() +  DownloadFileConf.getInstance().getExtensionName();
        File file = new File(path);
        if(!file.exists() || !file.canRead()){
            response.setContentType(ERROR_CONTENT_TYPE);
            try{
                PrintWriter printWriter = response.getWriter();
                printWriter.print("{\"message\":\"file not exist or not be read\"}");
                printWriter.flush();
                printWriter.close();
            }
            catch(IOException e){
                logger.error("file not exist or not be read");
            }
            return;
        }
        long dataLength = file.length();
        String targetName = rootNode.path("filename").asText();
        String fileName = !Strings.isNullOrEmpty(targetName) ? targetName :
                fileDownload.getContent().getType() + "_" + fileDownload.getContent().getShop_id() + "_" + fileDownload.getContent().getTime();
        ByteBuffer bytebuffer = ByteBuffer.allocate(4 * 1024);

        FileInputStream fileInputStream = null;
        BufferedOutputStream outputStream = null;
        int bytesCount = 0;
        try{
            fileInputStream = new FileInputStream(file);
            //文件名由客户端传入
            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            response.addHeader("Content-Length", "" + dataLength);
            response.setContentType("application/octet-stream;charset=UTF-8");
            outputStream = new BufferedOutputStream(response.getOutputStream());
            FileChannel fileChannel_from = fileInputStream.getChannel();
            while ((bytesCount = fileChannel_from.read(bytebuffer)) > 0) {
                //flip the buffer which set the limit to current position, and position to 0
                bytebuffer.flip();
                //write data from ByteBuffer to file
                outputStream.write(bytebuffer.array(), 0, bytesCount);
                //for the next read
                bytebuffer.clear();
            }
            outputStream.flush();
            fileChannel_from.close();
        }
        catch (FileNotFoundException e){
            logger.error(e);
            e.printStackTrace();
        }catch (UnsupportedEncodingException e){
            logger.error(e);
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }finally{
            try{
                Closeables.close(outputStream, true);
                Closeables.close(fileInputStream, true);
            }
            catch (IOException e){
                logger.error("stream close error");
            }
        }
    }

}