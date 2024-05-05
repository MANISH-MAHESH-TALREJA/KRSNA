package net.manish.navratri.asyncTask;

import android.os.AsyncTask;

import net.manish.navratri.interfaces.MessageListener;
import net.manish.navratri.interfaces.VideoListener;
import net.manish.navratri.item.ItemAbout;
import net.manish.navratri.item.ItemMessage;
import net.manish.navratri.item.ItemVideos;
import net.manish.navratri.util.Constant;
import net.manish.navratri.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadVideo extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private VideoListener videoListener;
    private ArrayList<ItemVideos> arrayList = new ArrayList<>();
    private String verifyStatus = "0", message = "";

    public LoadVideo(VideoListener videoListener, RequestBody requestBody) {
        this.videoListener = videoListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        videoListener.onStart();
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
                    String videoId = objJson.getString(Constant.LATEST_VIDEO_ID);
                    String videoTitle = objJson.getString(Constant.LATEST_VIDEO_TITLE);
                    String videoImage = objJson.getString(Constant.LATEST_VIDEO_IMAGE);
                    String videoLink = objJson.getString(Constant.LATEST_VIDEO_URL);
                    String videoType = objJson.getString(Constant.LATEST_VIDEO_TYPE);
                    String videoLayout = objJson.getString(Constant.LATEST_VIDEO_LAYOUT);


                    ItemVideos itemVideos = new ItemVideos(videoId, videoType, videoTitle, videoLink, videoImage, videoLayout);
                    arrayList.add(itemVideos);
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
        videoListener.onEnd(s, verifyStatus, message, arrayList);
        super.onPostExecute(s);
    }
}