package net.manish.navratri.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.manish.navratri.adapter.AdapterRingtone;
import net.manish.navratri.adapter.AdapterVideo;
import net.manish.navratri.asyncTask.LoadRingtone;
import net.manish.navratri.R;
import net.manish.navratri.asyncTask.LoadVideo;
import net.manish.navratri.interfaces.RingtoneListener;
import net.manish.navratri.interfaces.VideoListener;
import net.manish.navratri.item.ItemRingtone;
import net.manish.navratri.item.ItemVideos;
import net.manish.navratri.util.Constant;
import net.manish.navratri.util.Methods;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class FragmentVideo extends Fragment
{

    private Methods methods;
    private RecyclerView recyclerView;
    private ArrayList<ItemVideos> arrayList;
    private AdapterVideo adapterVideo;
    private CircularProgressBar progressBar;

    private TextView textView_empty;
    private AppCompatButton button_try;
    private LinearLayout ll_empty;
    private String errr_msg;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.fitness_video_fragment, container, false);

        methods = new Methods(getActivity());

        arrayList = new ArrayList<>();

        ll_empty = rootView.findViewById(R.id.ll_empty);
        textView_empty = rootView.findViewById(R.id.textView_empty_msg);
        button_try = rootView.findViewById(R.id.button_empty_try);
        errr_msg = getString(R.string.no_ringtones_found);

        progressBar = rootView.findViewById(R.id.pb_video);
        recyclerView = rootView.findViewById(R.id.rv_video);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        button_try.setOnClickListener(v -> loadData());

        loadData();

        setHasOptionsMenu(true);
        return rootView;
    }

    private void loadData()
    {
        if (methods.isNetworkAvailable())
        {
            LoadVideo loadVideo = new LoadVideo(new VideoListener()
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
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemVideos> arrayListVideos)
                {
                    if (getActivity() != null)
                    {
                        if (success.equals("1"))
                        {
                            if (!verifyStatus.equals("-1"))
                            {
                                arrayList.addAll(arrayListVideos);
                                errr_msg = getString(R.string.no_videos_found);
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
            }, methods.getAPIRequest(Constant.METHOD_VIDEOS, 0, "", "", ""));

            loadVideo.execute();
        } else
        {
            errr_msg = getString(R.string.net_not_conn);
            setEmpty();
        }
    }


    private void setAdapter()
    {
        adapterVideo = new AdapterVideo(getActivity(), arrayList, methods.getScreenWidth());
        recyclerView.setAdapter(adapterVideo);
        setEmpty();
    }

    private void setEmpty()
    {
        progressBar.setVisibility(View.GONE);
        if (arrayList.size() > 0)
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