package net.manish.navratri.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import net.manish.navratri.fab.FloatingActionButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import net.manish.navratri.R;
import net.manish.navratri.asyncTask.LoadWallpapers;
import net.manish.navratri.interfaces.InterAdListener;
import net.manish.navratri.interfaces.WallpaperListener;
import net.manish.navratri.item.ItemWallpaper;
import net.manish.navratri.util.Constant;
import net.manish.navratri.util.Methods;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class SingleWallpaper extends AppCompatActivity {

    Methods methods;
    int position;
    ViewPager viewpager;
    FloatingActionButton button_save, button_share_image, button_setWall;
    LinearLayout ll_adView;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_wallpaper);

        position = getIntent().getIntExtra("pos", 0);

        methods = new Methods(this/*, new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                methods.saveImage(Constant.arrayList_wallpaper.get(viewpager.getCurrentItem()).getImageBig(), type, "");
            }
        }*/);
        methods.setStatusColor(getWindow());
        methods.forceRTLIfSupported(getWindow());

        Toolbar toolbar = findViewById(R.id.toolbar_singwall);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.wallpaper));

        ll_adView = findViewById(R.id.ll_adView);
        //methods.showBannerAd(ll_adView);

        viewpager = findViewById(R.id.viewPager_wall);
        button_save = findViewById(R.id.button_save);
        button_share_image = findViewById(R.id.button_share_image);
        button_setWall = findViewById(R.id.button_imgdtls_setwall);

        button_setWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (methods.checkPer()) {
                    methods.saveImage(Constant.arrayList_wallpaper.get(viewpager.getCurrentItem()).getImageBig(), getString(R.string.set_as_wallpaper), "");
                    // methods.showInterAd(viewpager.getCurrentItem(), getString(R.string.set_as_wallpaper));
                }
            }
        });

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (methods.checkPer()) {
                    methods.saveImage(Constant.arrayList_wallpaper.get(viewpager.getCurrentItem()).getImageBig(), getString(R.string.download), "");
                    // methods.showInterAd(viewpager.getCurrentItem(), getString(R.string.download));
                }
            }
        });

        button_share_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (methods.checkPer()) {
                    methods.saveImage(Constant.arrayList_wallpaper.get(viewpager.getCurrentItem()).getImageBig(), getString(R.string.share), "");
                    /*methods.showInterAd(viewpager.getCurrentItem(), getString(R.string.share));*/
                }
            }
        });

        ImagePagerAdapter adapter = new ImagePagerAdapter();
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(position);

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                position = viewpager.getCurrentItem();
                loadViewed(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int position) {
            }

            @Override
            public void onPageScrollStateChanged(int position) {
            }
        });

        loadViewed(position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void loadViewed(int pos) {
        loadWallpapers(pos);
    }

    private void loadWallpapers(int pos) {
        if (methods.isNetworkAvailable()) {
            LoadWallpapers loadWallpapers = new LoadWallpapers(new WallpaperListener() {
                @Override
                public void onStart() {
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemWallpaper> arrayListNews) {
                }
            }, methods.getAPIRequest(Constant.METHOD_SINGLE_WALL, 0, "", Constant.arrayList_wallpaper.get(pos).getId(), ""));
            loadWallpapers.execute();
        }
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;

        ImagePagerAdapter() {
            inflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            return Constant.arrayList_wallpaper.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view.equals(object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            View imageLayout = inflater.inflate(R.layout.viewpager_item, container, false);
            assert imageLayout != null;
            ImageView imageView = imageLayout.findViewById(R.id.image);
            final ProgressBar spinner = imageLayout.findViewById(R.id.loading);

            Picasso.get()
                    .load(Constant.arrayList_wallpaper.get(position).getImageBig().replace(" ", "%20"))
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            spinner.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            spinner.setVisibility(View.GONE);
                        }
                    });

            container.addView(imageLayout, 0);
            return imageLayout;

        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
