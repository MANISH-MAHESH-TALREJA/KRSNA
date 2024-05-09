package net.manish.navratri.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import net.manish.navratri.R;
import net.manish.navratri.asyncTask.LoadQuiz;
import net.manish.navratri.interfaces.QuizListener;
import net.manish.navratri.item.ItemQuiz;
import net.manish.navratri.util.Constant;
import net.manish.navratri.util.Methods;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class QuizActivity extends AppCompatActivity implements OnClickListener {

    Methods methods;
    TextView textView_que, textView_que_no, textView_tot_que, textView_timer, textView_5050, textView_skip;
    Button button_skip, button_5050, button_okk;
    RadioButton radioButton_a, radioButton_b, radioButton_c, radioButton_d;
    RadioGroup radioGroup;
    ArrayList<ItemQuiz> arrayList;
    ArrayList<ItemQuiz> arrayList_temp;
    int pos = 0;
    Boolean isAnswered = false, isTime = false;
    int attempted = 0, wrong = 0, right = 0, total = 0;
    int life_5050 = 3, life_skip = 3;
    Boolean is5050 = true;
    Handler handler = new Handler();
    int timer = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        methods = new Methods(this);
        methods.setStatusColor(getWindow());
        methods.forceRTLIfSupported(getWindow());

        Toolbar toolbar = findViewById(R.id.toolbar_quiz);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        isTime = getIntent().getBooleanExtra("istime", false);
        total = getIntent().getIntExtra("que", 5);
        if (isTime) {
            timer = getIntent().getIntExtra("time", 30);
        }

        arrayList = new ArrayList<>();
        arrayList_temp = new ArrayList<>();

        textView_que = findViewById(R.id.textView_question);
        textView_que.setMovementMethod(new ScrollingMovementMethod());
        textView_que_no = findViewById(R.id.textView_ques_no);
        textView_tot_que = findViewById(R.id.textView_ques_total);
        textView_timer = findViewById(R.id.textView_time);
        textView_5050 = findViewById(R.id.textView_5050);
        textView_skip = findViewById(R.id.textView_skip);
        button_skip = findViewById(R.id.button_skip);
        button_5050 = findViewById(R.id.button_5050);
        button_okk = findViewById(R.id.button_okk);
        radioButton_a = findViewById(R.id.radioButton_A);
        radioButton_b = findViewById(R.id.radioButton_B);
        radioButton_c = findViewById(R.id.radioButton_C);
        radioButton_d = findViewById(R.id.radioButton_D);
        radioGroup = findViewById(R.id.radioGroup);

        radioButton_a.setButtonDrawable(android.R.color.transparent);
        radioButton_b.setButtonDrawable(android.R.color.transparent);
        radioButton_c.setButtonDrawable(android.R.color.transparent);
        radioButton_d.setButtonDrawable(android.R.color.transparent);

        button_okk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioGroup.getCheckedRadioButtonId() != -1) {
                    if (!isAnswered) {
                        attempted = attempted + 1;
                        isAnswered = true;
                        RadioButton rd = findViewById(radioGroup.getCheckedRadioButtonId());
                        String selected = rd.getText().toString();
                        if (selected.equals(arrayList.get(pos).getAnswer())) {
                            right = right + 1;
                            rd.setBackground(getResources().getDrawable(R.drawable.bg_radio_gradient_green));
                            rd.setCompoundDrawablesWithIntrinsicBounds(R.drawable.thumb_green, 0, 0, 0);
                            rd.setTextColor(getResources().getColor(R.color.white));
                        } else {
                            wrong = wrong + 1;
                            rd.setBackground(getResources().getDrawable(R.drawable.bg_radio_gradient_red));
                            rd.setCompoundDrawablesWithIntrinsicBounds(R.drawable.thumb_red, 0, 0, 0);
                            rd.setTextColor(getResources().getColor(R.color.white));
                            if (radioButton_a.getText().toString().equals(arrayList.get(pos).getAnswer())) {
                                radioButton_a.setBackground(getResources().getDrawable(R.drawable.bg_radio_gradient_green));
                                radioButton_a.setTextColor(getResources().getColor(R.color.white));
                                radioButton_a.setCompoundDrawablesWithIntrinsicBounds(R.drawable.thumb_green, 0, 0, 0);
                            } else if (radioButton_b.getText().toString().equals(arrayList.get(pos).getAnswer())) {
                                radioButton_b.setBackground(getResources().getDrawable(R.drawable.bg_radio_gradient_green));
                                radioButton_b.setTextColor(getResources().getColor(R.color.white));
                                radioButton_b.setCompoundDrawablesWithIntrinsicBounds(R.drawable.thumb_green, 0, 0, 0);
                            } else if (radioButton_c.getText().toString().equals(arrayList.get(pos).getAnswer())) {
                                radioButton_c.setBackground(getResources().getDrawable(R.drawable.bg_radio_gradient_green));
                                radioButton_c.setTextColor(getResources().getColor(R.color.white));
                                radioButton_c.setCompoundDrawablesWithIntrinsicBounds(R.drawable.thumb_green, 0, 0, 0);
                            } else if (radioButton_d.getText().toString().equals(arrayList.get(pos).getAnswer())) {
                                radioButton_d.setBackground(getResources().getDrawable(R.drawable.bg_radio_gradient_green));
                                radioButton_d.setTextColor(getResources().getColor(R.color.white));
                                radioButton_d.setCompoundDrawablesWithIntrinsicBounds(R.drawable.thumb_green, 0, 0, 0);
                            }
                        }

                        new Handler().postDelayed(() ->
                        {
                            radioGroup.clearCheck();
                            nextQues();
                        }, 2000);
                    }
                } else {
                    Toast.makeText(QuizActivity.this, getString(R.string.select_answer), Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_5050.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isAnswered) {
                    if (is5050) {
                        if (life_5050 != 0) {
                            life_5050 = life_5050 - 1;
                            set5050();
                            random();
                            is5050 = false;
                            radioGroup.clearCheck();
                        } else {
                            Toast.makeText(QuizActivity.this, getString(R.string.not_available_5050), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(QuizActivity.this, getString(R.string.already_used_5050), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        button_skip.setOnClickListener(this);

        getFromDatabase();
    }

    @Override
    public void onClick(View v) {
        int key = v.getId();
        if (key == R.id.button_skip) {
            if (!isAnswered) {
                if (life_skip != 0) {
                    life_skip = life_skip - 1;
                    setSkip();
                    nextQues();
                } else {
                    Toast.makeText(QuizActivity.this, getString(R.string.skip_not_available), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    private void getFromDatabase() {
        arrayList_temp.clear();
        arrayList.clear();

        loadQuiz();
    }

    private void setVariables() {

        textView_que_no.setText("" + (pos + 1));

        isAnswered = false;
        radioGroup.clearCheck();

        textView_que.setText(arrayList.get(pos).getQuestion());
        radioButton_a.setText(arrayList.get(pos).getA());
        radioButton_b.setText(arrayList.get(pos).getB());
        radioButton_c.setText(arrayList.get(pos).getC());
        radioButton_d.setText(arrayList.get(pos).getD());

        radioButton_a.setCompoundDrawablesWithIntrinsicBounds(R.drawable.selector_radio_thumb, 0, 0, 0);
        radioButton_b.setCompoundDrawablesWithIntrinsicBounds(R.drawable.selector_radio_thumb, 0, 0, 0);
        radioButton_c.setCompoundDrawablesWithIntrinsicBounds(R.drawable.selector_radio_thumb, 0, 0, 0);
        radioButton_d.setCompoundDrawablesWithIntrinsicBounds(R.drawable.selector_radio_thumb, 0, 0, 0);

        radioButton_a.setBackground(getResources().getDrawable(R.drawable.bg_radio_gradient_grey));
        radioButton_b.setBackground(getResources().getDrawable(R.drawable.bg_radio_gradient_grey));
        radioButton_c.setBackground(getResources().getDrawable(R.drawable.bg_radio_gradient_grey));
        radioButton_d.setBackground(getResources().getDrawable(R.drawable.bg_radio_gradient_grey));

        radioButton_a.setTextColor(getResources().getColor(R.color.black));
        radioButton_b.setTextColor(getResources().getColor(R.color.black));
        radioButton_c.setTextColor(getResources().getColor(R.color.black));
        radioButton_d.setTextColor(getResources().getColor(R.color.black));

        radioButton_a.setEnabled(true);
        radioButton_b.setEnabled(true);
        radioButton_c.setEnabled(true);
        radioButton_d.setEnabled(true);
    }

    private void loadQuiz() {
        if (methods.isNetworkAvailable()) {
            LoadQuiz loadQuiz = new LoadQuiz(new QuizListener() {
                ProgressDialog pDialog;

                @Override
                public void onStart() {
                    pDialog = new ProgressDialog(QuizActivity.this);
                    pDialog.setMessage(getString(R.string.loading));
                    pDialog.setCancelable(false);
                    pDialog.show();
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemQuiz> arrayListQuiz) {
                    pDialog.dismiss();
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            arrayList_temp.addAll(arrayListQuiz);
                            fillArrayRandomly();
                            textView_tot_que.setText("" + arrayList.size());
                            setVariables();
                            if (isTime) {
                                startTimer();
                            }
                        } else {
                            methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                        }
                    } else {
                        Toast.makeText(QuizActivity.this, getString(R.string.server_no_conn), Toast.LENGTH_SHORT).show();
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_QUIZ, 0, "", "", ""));
            loadQuiz.execute();
        } else {
            Toast.makeText(QuizActivity.this, getString(R.string.net_not_conn), Toast.LENGTH_SHORT).show();
        }
    }

    public void fillArrayRandomly() {
        if (arrayList_temp.size() < total) {
            total = arrayList_temp.size();
        }
        List<Integer> generated = new ArrayList<>();
        Random random = new Random(System.currentTimeMillis());
        arrayList.clear();

        for (int i = 0; i < total; i++) {
            while (true) {
                Integer next = random.nextInt(arrayList_temp.size());
                if (!generated.contains(next)) {
                    arrayList.add(arrayList_temp.get(next));
                    generated.add(next);
                    break;
                }
            }
        }
    }

    private void setTimer() {
        timer = timer - 1;
        textView_timer.setText("" + timer);
        if (timer == 0) {
            showResult();
        } else {
            startTimer();
        }
    }

    Runnable run = new Runnable() {
        public void run() {
            setTimer();
        }
    };

    private void startTimer() {
        handler.postDelayed(run, 1000);
    }

    private void stopTimer() {
        handler.removeCallbacks(run);
    }

    private void set5050() {
        textView_5050.setText(getString(R.string.a5050) + " - " + life_5050);
    }

    private void setSkip() {
        textView_skip.setText(getString(R.string.skip) + " - " + life_skip);
    }

    private void nextQues() {
        if (pos < arrayList.size() - 1) {
            is5050 = true;
            pos = pos + 1;
            setVariables();
            isAnswered = false;
        } else {
            showResult();
        }
    }

    public void random() {
        int tot = 0;
        int copy = 0;
        Random random = new Random();
        int a = random.nextInt(5 - 1) + 1;

        String op1 = arrayList.get(pos).getA();
        String op2 = arrayList.get(pos).getB();
        String op3 = arrayList.get(pos).getC();
        String op4 = arrayList.get(pos).getD();
        String ans = arrayList.get(pos).getAnswer();

        while (tot < 2) {
            if (a == 1) {
                if (!ans.equals(op1) && copy != a) {
                    radioButton_a.setEnabled(false);
                    radioButton_a.setTextColor(ContextCompat.getColor(QuizActivity.this, R.color.text_disable));
                    tot = tot + 1;
                    copy = a;
                }
            } else if (a == 2) {
                if (!ans.equals(op2) && copy != a) {
                    radioButton_b.setEnabled(false);
                    radioButton_b.setTextColor(ContextCompat.getColor(QuizActivity.this, R.color.text_disable));
                    tot = tot + 1;
                    copy = a;
                }
            } else if (a == 3) {
                if (!ans.equals(op3) && copy != a) {
                    radioButton_c.setEnabled(false);
                    radioButton_c.setTextColor(ContextCompat.getColor(QuizActivity.this, R.color.text_disable));
                    tot = tot + 1;
                    copy = a;
                }
            } else {
                if (!ans.equals(op4) && copy != a) {
                    radioButton_d.setEnabled(false);
                    radioButton_d.setTextColor(ContextCompat.getColor(QuizActivity.this, R.color.text_disable));
                    tot = tot + 1;
                    copy = a;
                }
            }
            a = random.nextInt(5 - 1) + 1;
        }
    }

    private void showResult() {

        stopTimer();

        final Dialog dialog = new Dialog(QuizActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_result);
        dialog.setCancelable(false);

        TextView textView_tot = dialog.findViewById(R.id.textView_tot_que);
        TextView textView_attemped = dialog.findViewById(R.id.textView_attempted);
        TextView textView_right = dialog.findViewById(R.id.textView_right);
        TextView textView_wrong = dialog.findViewById(R.id.textView_wrong);
        Button button_play = dialog.findViewById(R.id.button_playagain);
        Button button_exit = dialog.findViewById(R.id.button_exit);

        textView_tot.setText("" + arrayList.size());
        textView_attemped.setText("" + attempted);
        textView_right.setText("" + right);
        textView_wrong.setText("" + wrong);

        button_play.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                pos = 0;
                attempted = 0;
                right = 0;
                wrong = 0;
                is5050 = true;
                life_5050 = 2;
                life_skip = 2;
                if (isTime) {
                    timer = getIntent().getIntExtra("time", 30);
                    startTimer();
                }
                set5050();
                setSkip();
                fillArrayRandomly();
                setVariables();

                try {
                    radioGroup.clearCheck();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isAnswered = false;
                dialog.dismiss();
            }
        });

        button_exit.setOnClickListener(v ->
        {
            dialog.dismiss();
            finish();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        dialog.show();
    }

    private void showExitDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(QuizActivity.this)
                .setTitle(getString(R.string.cancel_quiz))
                .setMessage(getString(R.string.sure_cancel_quiz))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        handler.removeCallbacks(run);
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(run);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    }
}