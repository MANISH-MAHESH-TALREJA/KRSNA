package net.manish.navratri.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.manish.navratri.activity.MessageActivity;
import net.manish.navratri.R;
import net.manish.navratri.item.ItemMessage;
import net.manish.navratri.util.Constant;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class AdapterMessage extends RecyclerView.Adapter<AdapterMessage.MyViewHolder>
{
    private final Context context;
    private ArrayList<ItemMessage> arrayList;

    public AdapterMessage(Context context, ArrayList<ItemMessage> arrayList)
    {
        this.arrayList = arrayList;
        this.context = context;
    }

    public void filterList(ArrayList<ItemMessage> filterList)
    {
        arrayList = filterList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView textView_message;
        TextView number;

        MyViewHolder(View view)
        {
            super(view);
            textView_message = view.findViewById(R.id.tv_message);
            number = view.findViewById(R.id.shlokaNumber);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {

        ItemMessage itemMessage = arrayList.get(position);

        holder.textView_message.setText(itemMessage.getMessage());
        holder.number.setText(itemMessage.getNumber());
        holder.textView_message.setOnClickListener(view ->
        {
            Constant.arrayList_message.clear();
            Constant.arrayList_message.addAll(arrayList);

            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("pos", holder.getAdapterPosition());
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    @Override
    public int getItemCount()
    {
        return arrayList.size();
    }

}