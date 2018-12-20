package com.csc.capturetool.myapplication.http;

import com.csc.capturetool.myapplication.modules.action.model.BlueToothDataBean;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * @author yangkun
 * @time 2018/10/23
 */
public class ResultJsonDeser implements JsonDeserializer<BlueToothDataBean> {

    @Override
    public BlueToothDataBean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        BlueToothDataBean response = new BlueToothDataBean();
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            int code = jsonObject.get("code").getAsInt();
            response.setCode(code);
            if (code != 0) {
                return response;
            }
            //            response.setData(jsonObject.get("data").getAsString());
            response.setDevid(jsonObject.get("devid").getAsString());
            response.setUdpsig(jsonObject.get("udpsig").getAsString());
            response.setData1(jsonObject.get("data1").getAsString());
            //结果为成功结果才解析data数据，否则return
            //            Type itemType = ((ParameterizedType) typeOfT).getActualTypeArguments()[0];
            //            response.setData(context.deserialize(jsonObject.get("data"), itemType));
            response.setData(jsonObject.get("data").getAsString());
            return response;
        }
        return response;
    }
}
