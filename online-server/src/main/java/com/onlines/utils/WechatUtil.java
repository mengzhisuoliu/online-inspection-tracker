package com.onlines.utils;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
public class WechatUtil {
    private static final Logger logger= LoggerFactory.getLogger(WechatUtil.class);
    public static String setMsgContent(ITestResult result){
        String caseDescription = result.getMethod().getDescription();
        Object[] caseParameters = result.getParameters();
        String msgContent = "";
        msgContent = msgContent + "提醒：线上巡检有报警！";
        if (!caseDescription.isEmpty()){
            msgContent = msgContent + "\n" +  "测试描述：" + caseDescription;
        }
        msgContent = msgContent + "\n" + "测试类和方法：" + result.getMethod().toString();
        if (caseParameters.length != 0){
            msgContent = msgContent + "\n" + "测试用例：";
            for (int i = 0; i < 4; i++){
                msgContent = msgContent + result.getParameters()[i].toString() + "；";
            }
        }
        return msgContent;
    }

    public static String sendMsg(ITestResult result){
        String access_token = getToken(result);
        if(access_token!=null) {
            String msgContent = setMsgContent(result);
            String url = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?" + access_token;
            JSONObject content = new JSONObject();
            content.put("content", msgContent);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msgtype", "text");
            jsonObject.put("text", content);
            logger.info("入参：" + jsonObject.toJSONString());

            HttpResponse res = HttpRequest.post(url).header("Content-Type", "application/json").body(jsonObject.toJSONString()).execute();
            return res.body();
        }
        return null;
    }

    public static String setMsgContentPic(String testUrl, int id,String datum, String title){
        String msgContent = "";
        msgContent = msgContent + "提醒：线上巡检有报警，像素对比异常！";
        msgContent = msgContent + "\n" + "巡检测试用例：" + "\n" + title;
        msgContent = msgContent + "\n" + "图片像素对比异常url地址：" + "\n" + testUrl;
        msgContent = msgContent + "\n" + "图片异常地址：" + "\n" + datum;
        return msgContent;
    }

    public static String sendMsgPic(String testUrl, int id, String datum, String title, String wechatKey){
        if(wechatKey==null) {
            return null;
        }
        String access_token = "key=" + wechatKey;
        String msgContent = setMsgContentPic(testUrl,id,datum,title);
        String url = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?" + access_token;
        JSONObject content = new JSONObject();
        content.put("content", msgContent);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msgtype", "text");
        jsonObject.put("text", content);
        logger.info("入参：" + jsonObject.toJSONString());

        HttpResponse DingResponse = HttpRequest.post(url).header("Content-Type", "application/json").body(jsonObject.toJSONString()).execute();
        logger.info(DingResponse.body());
        return DingResponse.body();
    }

    public static String getToken(ITestResult result){
        String access_token;
        Object[] caseParameters = result.getParameters();

        if (caseParameters.length != 0) {
            if (result.getParameters()[5] != null) {
                access_token = "key=" + result.getParameters()[5];
                return access_token;
            }
        }
        return null;
    }

}
