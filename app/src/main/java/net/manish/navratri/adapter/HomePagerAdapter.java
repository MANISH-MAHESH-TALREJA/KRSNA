package net.manish.navratri.adapter;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.tiagosantos.enchantedviewpager.EnchantedViewPager;
import com.tiagosantos.enchantedviewpager.EnchantedViewPagerAdapter;

import net.manish.navratri.R;
import net.manish.navratri.activity.SingleWallpaper;
import net.manish.navratri.item.ItemWallpaper;
import net.manish.navratri.util.Constant;
import net.manish.navratri.util.Methods;

import java.util.ArrayList;

public class HomePagerAdapter extends EnchantedViewPagerAdapter
{

    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<ItemWallpaper> arrayList;
    private Methods methods;

    public HomePagerAdapter(Context context, ArrayList<ItemWallpaper> arrayList)
    {
        super(arrayList);
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.arrayList = arrayList;
        methods = new Methods(context);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position)
    {
        View mCurrentView = inflater.inflate(R.layout.layout_home_pager, container, false);

        RoundedImageView imageView = mCurrentView.findViewById(R.id.imageView_home_vp);

        Picasso.get()
                .load(arrayList.get(position).getImageBig())
                .resize(650, 450)
                .placeholder(R.mipmap.app_icon)
                .into(imageView);

        mCurrentView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Constant.arrayList_wallpaper.clear();
                Constant.arrayList_wallpaper.addAll(arrayList);
                Intent intent = new Intent(mContext, SingleWallpaper.class);
                intent.putExtra("pos", position);
                intent.putExtra("pos", Constant.arrayList_wallpaper.get(position).getLayout());
                mContext.startActivity(intent);
                /*methods.showInterAd(position, "");*/
            }
        });

        mCurrentView.setTag(EnchantedViewPager.ENCHANTED_VIEWPAGER_POSITION + position);
        container.addView(mCurrentView);

        return mCurrentView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
    {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(@NonNull Object object)
    {
        return POSITION_NONE;
    }

    @Override
    public int getCount()
    {
        return 5;
    }

}