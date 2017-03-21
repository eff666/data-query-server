package com.shuyun.query.util;

import com.shuyun.crypt.CryptTools;
import com.shuyun.crypt.taobaosdk.com.taobao.api.security.SecurityClient;
import com.shuyun.query.meta.EsQueryConf;
import org.apache.log4j.Logger;
import org.elasticsearch.common.Strings;


/**
 * Created by shuyun on 2016/8/24.
 */
public class ShuyunCryptUtil {

    private static Logger logger = Logger.getLogger(ShuyunCryptUtil.class);

    /** 加密数据类型_收货人 */
    public static final String TYPE_RECEIVER_NAME = SecurityClient.RECEIVER_NAME;
    /** 加密数据类型_昵称 */
    public static final String TYPE_NICK = SecurityClient.NICK;
    /** 加密数据类型_手机 */
    public static final String TYPE_PHONE = SecurityClient.PHONE;
    /** 加密数据类型_其它 */
    public static final String TYPE_NORMAL = SecurityClient.NORMAL;

    //加密
    public static String encrypt(String dataType, String data){
        if(Strings.isNullOrEmpty(data)){
            return data;
        }
        String dataEncrypt = "";
        if(EsQueryConf.getInstance().getNeedCryptName().contains(dataType)){
            dataEncrypt = CryptTools.encrypt(data, TYPE_RECEIVER_NAME);
        } else if(EsQueryConf.getInstance().getNeedCryptNick().contains(dataType)){
            dataEncrypt = CryptTools.encrypt(data, TYPE_NICK);
        }else if(EsQueryConf.getInstance().getNeedCryptPhone().contains(dataType)){
            dataEncrypt = CryptTools.encrypt(data, TYPE_PHONE);
        } else {
            dataEncrypt = data;
        }
        return dataEncrypt;
    }


    //解密
    public static String decrypt(String dataType, String data){
        if(Strings.isNullOrEmpty(data)){
            return data;
        }

        String dataDecrypt = "";
        try {
            if (EsQueryConf.getInstance().getNeedCryptName().contains(dataType)) {
                dataDecrypt = CryptTools.decrypt(data, TYPE_RECEIVER_NAME);
            } else if (EsQueryConf.getInstance().getNeedCryptNick().contains(dataType)) {
                dataDecrypt = CryptTools.decrypt(data, TYPE_NICK);
            } else if (EsQueryConf.getInstance().getNeedCryptPhone().contains(dataType)) {
                dataDecrypt = CryptTools.decrypt(data, TYPE_PHONE);
            } else {
                dataDecrypt = data;
            }
        } catch (Exception e){
            return data;
        }
        return dataDecrypt;
    }

    /** 判断数据是否是密文 */
    public static boolean isEncrypt(String dataType,String data) {
        boolean flag = false;
        try {
            if (EsQueryConf.getInstance().getNeedCryptName().contains(dataType)) {
                flag = CryptTools.isEncrypt(data, TYPE_RECEIVER_NAME);
            } else if (EsQueryConf.getInstance().getNeedCryptNick().contains(dataType)) {
                flag = CryptTools.isEncrypt(data, TYPE_NICK);
            } else if (EsQueryConf.getInstance().getNeedCryptPhone().contains(dataType)) {
                flag = CryptTools.isEncrypt(data, TYPE_PHONE);
            }
        }catch (Exception e){
            logger.error("data isEncrypt error, cause=" + e.getCause() + "\n" + e.getMessage());
            return false;
        }
        return flag;

    }
}
