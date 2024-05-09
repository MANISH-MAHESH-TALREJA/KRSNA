package net.manish.navratri.asyncTask;

import android.os.AsyncTask;

import net.manish.navratri.interfaces.QuizListener;
import net.manish.navratri.item.ItemQuiz;
import net.manish.navratri.util.Constant;
import net.manish.navratri.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadQuiz extends AsyncTask<String, String, String>
{

    private RequestBody requestBody;
    private QuizListener quizListener;
    private ArrayList<ItemQuiz> arrayList = new ArrayList<>();
    private String verifyStatus = "0", message = "";

    public LoadQuiz(QuizListener quizListener, RequestBody requestBody)
    {
        this.quizListener = quizListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute()
    {
        quizListener.onStart();
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
                    String id = objJson.getString(Constant.QUIZ_ID);
                    String ques = objJson.getString(Constant.QUIZ_QUES);
                    String a = objJson.getString(Constant.QUIZ_A);
                    String b = objJson.getString(Constant.QUIZ_B);
                    String c = objJson.getString(Constant.QUIZ_C);
                    String d = objJson.getString(Constant.QUIZ_D);
                    String answer = objJson.getString(Constant.QUIZ_ANS);

                    ItemQuiz itemQuiz = new ItemQuiz(id, ques, a, b, c, d, answer);
                    arrayList.add(itemQuiz);
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
        quizListener.onEnd(s, verifyStatus, message, arrayList);
        super.onPostExecute(s);
    }
}