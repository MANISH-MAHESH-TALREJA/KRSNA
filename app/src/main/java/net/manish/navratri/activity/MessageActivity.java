package net.manish.navratri.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.manish.navratri.fab.FloatingActionButton;

import net.manish.navratri.R;
import net.manish.navratri.interfaces.InterAdListener;
import net.manish.navratri.util.Constant;
import net.manish.navratri.util.Methods;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class MessageActivity extends AppCompatActivity {

    Methods methods;
    Toolbar toolbar;
    FloatingActionButton button_share, button_copy;
    ViewPager viewPager;
    MessagePagerAdapter viewpager_Adapter;
    int pos = 0;
    LinearLayout adLayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        methods = new Methods(this/*, new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                switch (type) {
                    case "copy":
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("message", Constant.arrayList_message.get(viewPager.getCurrentItem()).getMessage());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(MessageActivity.this, getString(R.string.message_copied), Toast.LENGTH_SHORT).show();
                        break;
                    case "share":
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "" + Constant.arrayList_message.get(viewPager.getCurrentItem()).getMessage());
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                        break;
                }
            }
        }*/);
        methods.setStatusColor(getWindow());
        methods.forceRTLIfSupported(getWindow());

        toolbar = findViewById(R.id.toolbar_message);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.colorPrimary));

        adLayout = findViewById(R.id.ll_adView);

        button_share = findViewById(R.id.button_share_sms);
        button_copy = findViewById(R.id.button_copy_sms);
        viewPager = findViewById(R.id.viewPager_message);
        viewpager_Adapter = new MessagePagerAdapter();

        pos = getIntent().getIntExtra("pos", 0);

        viewPager.setAdapter(viewpager_Adapter);
        viewPager.setCurrentItem(pos);

        button_share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "" + Constant.arrayList_message.get(viewPager.getCurrentItem()).getMessage());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                // methods.showInterAd(viewPager.getCurrentItem(), "share");
            }
        });

        button_copy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("message", Constant.arrayList_message.get(viewPager.getCurrentItem()).getMessage());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MessageActivity.this, getString(R.string.message_copied), Toast.LENGTH_SHORT).show();
                // methods.showInterAd(viewPager.getCurrentItem(),"copy");
            }
        });
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

    private class MessagePagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;

        MessagePagerAdapter() {
            inflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            return Constant.arrayList_message.size();

        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view.equals(object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            View imageLayout = inflater.inflate(R.layout.layout_vp_message, container, false);

            TextView textView = imageLayout.findViewById(R.id.text_full_msg);
            textView.setText(Constant.arrayList_message.get(position).getMessage());

            container.addView(imageLayout, 0);
            return imageLayout;

        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
