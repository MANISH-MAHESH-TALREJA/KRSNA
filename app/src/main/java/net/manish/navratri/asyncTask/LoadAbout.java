package net.manish.navratri.asyncTask;

import android.os.AsyncTask;

import net.manish.navratri.interfaces.AboutListener;
import net.manish.navratri.item.ItemAbout;
import net.manish.navratri.util.Constant;
import net.manish.navratri.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class LoadAbout extends AsyncTask<String, String, String>
{

    private RequestBody requestBody;
    private AboutListener aboutListener;
    private String message = "", verifyStatus = "0";

    public LoadAbout(AboutListener aboutListener, RequestBody requestBody)
    {
        this.aboutListener = aboutListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute()
    {
        aboutListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings)
    {
        try
        {
            String json = JsonUtils.okhttpPost(Constant.SERVER_URL, requestBody);
            JSONObject jsonObject = new JSONObject(json);

            if (jsonObject.has(Constant.TAG_ROOT))
            {
                JSONArray jsonArray = jsonObject.getJSONArray(Constant.TAG_ROOT);
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject c = jsonArray.getJSONObject(i);

                    if (!c.has(Constant.TAG_SUCCESS))
                    {
                        String appname = c.getString("app_name");
                        String applogo = c.getString("app_logo");
                        String email = c.getString("app_email");
                        String desc = c.getString("app_description");
                        String website = c.getString("app_website");
                        String appversion = c.getString("app_version");
                        String appauthor = c.getString("app_author");
                        String appcontact = c.getString("app_contact");
                        String privacy = c.getString("app_privacy_policy");
                        String developedby = c.getString("app_developed_by");

                        Constant.packageName = c.getString("package_name");

                        Constant.showUpdateDialog = c.getBoolean("app_update_status");
                        Constant.appVersion = c.getString("app_new_version");
                        Constant.appUpdateMsg = c.getString("app_update_desc");
                        Constant.appUpdateURL = c.getString("app_redirect_url");
                        Constant.appUpdateCancel = c.getBoolean("cancel_update_status");

                        Constant.itemAbout = new ItemAbout(appname, applogo, desc, appversion, appauthor, appcontact, email, website, privacy, developedby);
                    } else
                    {
                        verifyStatus = c.getString(Constant.TAG_SUCCESS);
                        message = c.getString(Constant.TAG_MSG);
                    }
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
        aboutListener.onEnd(s, verifyStatus, message);
        super.onPostExecute(s);
    }
}