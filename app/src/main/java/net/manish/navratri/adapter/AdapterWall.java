package net.manish.navratri.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import net.manish.navratri.R;
import net.manish.navratri.activity.SingleWallpaper;
import net.manish.navratri.item.ItemWallpaper;
import net.manish.navratri.util.Constant;
import net.manish.navratri.util.Methods;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class AdapterWall extends RecyclerView.Adapter
{

    Context context;
    ArrayList<ItemWallpaper> arrayList, arrayListAdLess;
    int imageWidth;
    Methods methods;


    public AdapterWall(Context context, ArrayList<ItemWallpaper> arrayList, ArrayList<ItemWallpaper> arrayListAdLess, int columnWidth)
    {
        this.arrayList = arrayList;
        this.arrayListAdLess = arrayListAdLess;
        this.imageWidth = columnWidth;
        this.context = context;
        methods = new Methods(context);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;

        MyViewHolder(View view)
        {
            super(view);
            imageView = view.findViewById(R.id.iv_wall);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_wallpaper, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position)
    {

        ItemWallpaper objLatestBean = arrayList.get(position);

        ((MyViewHolder) holder).imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ((MyViewHolder) holder).imageView.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageWidth));

        Picasso.get()
                .load(objLatestBean.getImageSmall().replace(" ", "%20"))
                .placeholder(R.mipmap.app_icon)
                .into(((MyViewHolder) holder).imageView);

        ((MyViewHolder) holder).imageView.setOnClickListener(view ->
        {
            Constant.arrayList_wallpaper.clear();
            if (arrayListAdLess.isEmpty())
            {
                Constant.arrayList_wallpaper.addAll(arrayList);
            } else
            {
                Constant.arrayList_wallpaper.addAll(arrayListAdLess);
            }
            Intent intent = new Intent(context, SingleWallpaper.class);
            intent.putExtra("pos", getPos(position));
            intent.putExtra("layout", Constant.arrayList_wallpaper.get(position).getLayout());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemViewType(int position)
    {
        if (arrayList.get(position) != null)
        {
            return position;
        } else
        {
            return -2;
        }
    }

    @Override
    public int getItemCount()
    {
        return arrayList.size();
    }

    public boolean isHeader(int position)
    {
        return arrayList.get(position) == null;
    }

    public int getPos(int position)
    {
        return arrayListAdLess.indexOf(arrayList.get(position));
    }

}