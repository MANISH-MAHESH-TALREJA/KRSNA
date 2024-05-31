package net.manish.navratri.activity;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.theartofdev.edmodo.cropper.CropImageView;

import net.manish.navratri.R;
import net.manish.navratri.util.Constant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SetAsWallpaperActivity extends AppCompatActivity
{

    Toolbar toolbar;
    CropImageView mCropImageView;
    Bitmap bmImg;
    ProgressDialog progressDialog;
    BottomSheetDialog dialog_desc;

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_as_wallpaper_activity);

        toolbar = this.findViewById(R.id.toolbar_setwall);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));

        mCropImageView = findViewById(R.id.iv_crop);
        mCropImageView.setImageUriAsync(Constant.uri_set);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        if (menuItem.getItemId() == android.R.id.home)
        {
            onBackPressed();
        } else
        {
            return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    private class SetWall extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute()
        {
            progressDialog.show();
            bmImg = mCropImageView.getCroppedImage();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings)
        {

            WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
            try
            {
                myWallpaperManager.setWallpaperOffsetSteps(0, 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                {
                    String locktype = strings[0];
                    switch (locktype)
                    {
                        case "home":
                            myWallpaperManager.setBitmap(bmImg, null, true, WallpaperManager.FLAG_SYSTEM);
                            break;
                        case "lock":
                            myWallpaperManager.setBitmap(bmImg, null, true, WallpaperManager.FLAG_LOCK);
                            break;
                        case "all":
                            myWallpaperManager.setBitmap(bmImg);
                            break;
                    }
                } else
                {
                    myWallpaperManager.setBitmap(bmImg);
                }
            } catch (Exception e)
            {
                e.getLocalizedMessage();
                return "0";
            }
            return "1";
        }

        @Override
        protected void onPostExecute(String s)
        {
            progressDialog.dismiss();
            dialog_desc.dismiss();
            if (s.equals("1"))
            {
                Toast.makeText(SetAsWallpaperActivity.this, getString(R.string.wallpaper_set), Toast.LENGTH_SHORT).show();
                finish();
            } else
            {
                Toast.makeText(SetAsWallpaperActivity.this, getString(R.string.err_set_wallpaper), Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }

    public void showBottomSheetDialog(View view1)
    {
        View view = getLayoutInflater().inflate(R.layout.layout_set_wall, null);

        dialog_desc = new BottomSheetDialog(this);
        dialog_desc.setContentView(view);
        Objects.requireNonNull(dialog_desc.getWindow()).findViewById(com.google.android.material.R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        dialog_desc.show();

        TextView textView_home = dialog_desc.findViewById(R.id.tv_set_home);
        TextView textView_lock = dialog_desc.findViewById(R.id.tv_set_lock);
        TextView textView_all = dialog_desc.findViewById(R.id.tv_set_all);

        assert textView_home != null;
        textView_home.setOnClickListener(v -> new SetWall().execute("home"));

        assert textView_lock != null;
        textView_lock.setOnClickListener(v -> new SetWall().execute("lock"));

        assert textView_all != null;
        textView_all.setOnClickListener(v -> new SetWall().execute("all"));
    }
}
