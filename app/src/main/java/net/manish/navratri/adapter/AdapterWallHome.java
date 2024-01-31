package net.manish.navratri.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import net.manish.navratri.R;
import net.manish.navratri.item.ItemWallpaper;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterWallHome extends RecyclerView.Adapter<AdapterWallHome.MyViewHolder> {

    private ArrayList<ItemWallpaper> arrayList;
    private int imageWidth;

    public AdapterWallHome(ArrayList<ItemWallpaper> arrayList, int columnWidth) {
        this.arrayList = arrayList;
        this.imageWidth=columnWidth;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_wallpaper_home, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.imageView.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageWidth));
        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Picasso.get()
                .load(arrayList.get(position).getImageSmall())
                .placeholder(R.mipmap.app_icon)
                .into(holder.imageView);

    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        if(arrayList.size() >= 10) {
            return 10;
        } else {
            return arrayList.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.iv_wall);
        }
    }
}