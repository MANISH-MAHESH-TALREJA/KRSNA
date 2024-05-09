package net.manish.navratri.asyncTask;

import android.os.AsyncTask;

import net.manish.navratri.interfaces.MessageListener;
import net.manish.navratri.item.ItemMessage;
import net.manish.navratri.util.Constant;
import net.manish.navratri.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadMessage extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private MessageListener messageListener;
    private ArrayList<ItemMessage> arrayList = new ArrayList<>();
    private String verifyStatus = "0", message = "";

    public LoadMessage(MessageListener messageListener, RequestBody requestBody) {
        this.messageListener = messageListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        messageListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = JsonUtils.okhttpPost(Constant.SERVER_URL, requestBody);
            JSONObject mainJson = new JSONObject(json);
            JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objJson = jsonArray.getJSONObject(i);

                if (!objJson.has(Constant.TAG_SUCCESS)) {
                    String id = objJson.getString(Constant.LATESTMSG_ID);
                    String message = objJson.getString(Constant.LATESTMSG_URL);
                    String number = objJson.getString(Constant.LATESTMSG_NUM);

                    ItemMessage itemMessage = new ItemMessage(id, message, number);
                    arrayList.add(itemMessage);
                } else {
                    verifyStatus = objJson.getString(Constant.TAG_SUCCESS);
                    message = objJson.getString(Constant.TAG_MSG);
                }
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        messageListener.onEnd(s, verifyStatus, message, arrayList);
        super.onPostExecute(s);
    }
}