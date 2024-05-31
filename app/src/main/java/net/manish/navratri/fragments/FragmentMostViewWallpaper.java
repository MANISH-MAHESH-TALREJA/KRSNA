package net.manish.navratri.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.manish.navratri.adapter.AdapterWall;
import net.manish.navratri.asyncTask.LoadWallpapers;
import net.manish.navratri.R;
import net.manish.navratri.interfaces.WallpaperListener;
import net.manish.navratri.item.ItemWallpaper;
import net.manish.navratri.util.Constant;
import net.manish.navratri.util.Methods;


import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class FragmentMostViewWallpaper extends Fragment
{

    private ArrayList<ItemWallpaper> arrayList;
    private AdapterWall adapter;
    private int columnWidth;
    private Methods methods;
    private CircularProgressBar progressBar;

    private GridLayoutManager lLayout;
    private RecyclerView recyclerView;

    private TextView textView_empty;
    private AppCompatButton button_try;
    private LinearLayout ll_empty;
    private String errr_msg;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.fragment_wallpaper, container, false);

        arrayList = new ArrayList<>();

        methods = new Methods(getActivity());

        columnWidth = (methods.getScreenWidth() - ((Constant.NUM_OF_COLUMNS + 1))) / Constant.NUM_OF_COLUMNS;

        lLayout = new GridLayoutManager(getActivity(), 3);
        lLayout.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
        {
            @Override
            public int getSpanSize(int position)
            {
                return adapter.isHeader(position) ? lLayout.getSpanCount() : 1;
            }
        });

        ll_empty = rootView.findViewById(R.id.ll_empty);
        textView_empty = rootView.findViewById(R.id.textView_empty_msg);
        button_try = rootView.findViewById(R.id.button_empty_try);
        errr_msg = getString(R.string.no_wallpaper_found);

        progressBar = rootView.findViewById(R.id.pb_wall);
        recyclerView = rootView.findViewById(R.id.rv_wallpaper);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(lLayout);

        button_try.setOnClickListener(v -> loadData());

        loadData();
        return rootView;
    }

    private void loadData()
    {
        if (methods.isNetworkAvailable())
        {
            LoadWallpapers loadWallpapers = new LoadWallpapers(new WallpaperListener()
            {
                @Override
                public void onStart()
                {
                    arrayList.clear();
                    ll_empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemWallpaper> arrayListWallpaper)
                {
                    if (getActivity() != null)
                    {

                        if (success.equals("1"))
                        {
                            if (!verifyStatus.equals("-1"))
                            {
                                arrayList.addAll(arrayListWallpaper);
                                errr_msg = getString(R.string.no_wallpaper_found);
                                setAdapter();
                            } else
                            {
                                methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                            }
                        } else
                        {
                            errr_msg = getString(R.string.server_no_conn);
                            setEmpty();
                        }
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_MOST_VIEWED_WALLPAPER, 0, "", "", ""));

            loadWallpapers.execute();
        } else
        {
            errr_msg = getString(R.string.net_not_conn);
            setEmpty();
        }
    }


    private void setAdapter()
    {
        adapter = new AdapterWall(getActivity(), arrayList, arrayList, columnWidth);
        recyclerView.setAdapter(adapter);
        setEmpty();
    }

    private void setEmpty()
    {
        progressBar.setVisibility(View.GONE);
        if (!arrayList.isEmpty())
        {
            recyclerView.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else
        {
            textView_empty.setText(errr_msg);
            recyclerView.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }
}