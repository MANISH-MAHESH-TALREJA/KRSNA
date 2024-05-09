package net.manish.navratri.asyncTask;

import android.os.AsyncTask;

import net.manish.navratri.interfaces.RingtoneListener;
import net.manish.navratri.item.ItemRingtone;
import net.manish.navratri.util.Constant;
import net.manish.navratri.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadRingtone extends AsyncTask<String, String, String>
{

    private RequestBody requestBody;
    private RingtoneListener ringtoneListener;
    private ArrayList<ItemRingtone> arrayList = new ArrayList<>();
    private String verifyStatus = "0", message = "";

    public LoadRingtone(RingtoneListener ringtoneListener, RequestBody requestBody)
    {
        this.ringtoneListener = ringtoneListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute()
    {
        ringtoneListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings)
    {
        try
        {
            String json = JsonUtils.okhttpPost(Constant.SERVER_URL, requestBody);
            JSONObject mainJson = new JSONObject(json);
            JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject objJson = jsonArray.getJSONObject(i);

                if (!objJson.has(Constant.TAG_SUCCESS))
                {
                    String id = objJson.getString(Constant.TAG_ID);
                    String name = objJson.getString(Constant.TAG_RING_NAME);
                    String tag = objJson.getString(Constant.TAG_TAGS);
                    String url = objJson.getString(Constant.TAG_RING_URL);
                    String download = "0";
                    if (objJson.has(Constant.TAG_RING_DOWNLOAD))
                    {
                        download = objJson.getString(Constant.TAG_RING_DOWNLOAD);
                    }

                    ItemRingtone itemRingtone = new ItemRingtone(id, name, tag, url, download);
                    arrayList.add(itemRingtone);
                } else
                {
                    verifyStatus = objJson.getString(Constant.TAG_SUCCESS);
                    message = objJson.getString(Constant.TAG_MSG);
                }
            }
            return "1";
        } catch (Exception e)
        {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s)
    {
        ringtoneListener.onEnd(s, verifyStatus, message, arrayList);
        super.onPostExecute(s);
    }
}