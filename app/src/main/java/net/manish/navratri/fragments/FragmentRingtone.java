package net.manish.navratri.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.manish.navratri.adapter.AdapterRingtone;
import net.manish.navratri.asyncTask.LoadRingtone;
import net.manish.navratri.R;
import net.manish.navratri.interfaces.RingtoneListener;
import net.manish.navratri.item.ItemRingtone;
import net.manish.navratri.util.Constant;
import net.manish.navratri.util.Methods;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class FragmentRingtone extends Fragment
{

    private Methods methods;
    private RecyclerView recyclerView;
    private ArrayList<ItemRingtone> arrayList;
    private AdapterRingtone adapterRingtone;
    private CircularProgressBar progressBar;

    private TextView textView_empty;
    private AppCompatButton button_try;
    private LinearLayout ll_empty;
    private String errr_msg;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.fragment_ringtone, container, false);

        methods = new Methods(getActivity());

        arrayList = new ArrayList<>();

        ll_empty = rootView.findViewById(R.id.ll_empty);
        textView_empty = rootView.findViewById(R.id.textView_empty_msg);
        button_try = rootView.findViewById(R.id.button_empty_try);
        errr_msg = getString(R.string.no_ringtones_found);

        progressBar = rootView.findViewById(R.id.pb_ringtone);
        recyclerView = rootView.findViewById(R.id.rv_ringtone);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        button_try.setOnClickListener(v -> loadData());

        loadData();

        setHasOptionsMenu(true);
        return rootView;
    }


    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.actionSearch);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                filter(newText);
                return false;
            }
        });
    }

    private void filter(String text)
    {
        ArrayList<ItemRingtone> filteredlist = new ArrayList<ItemRingtone>();
        for (ItemRingtone item : arrayList)
        {
            if (item.getName().toLowerCase().contains(text.toLowerCase()))
            {
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty())
        {
            Toast.makeText(getActivity(), "No Data Found..", Toast.LENGTH_SHORT).show();
        } else
        {
            adapterRingtone.filterList(filteredlist);
        }
    }


    private void loadData()
    {
        if (methods.isNetworkAvailable())
        {
            LoadRingtone loadRingtone = new LoadRingtone(new RingtoneListener()
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
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemRingtone> arrayListRingtone)
                {
                    if (getActivity() != null)
                    {
                        if (success.equals("1"))
                        {
                            if (!verifyStatus.equals("-1"))
                            {
                                arrayList.addAll(arrayListRingtone);
                                errr_msg = getString(R.string.no_ringtones_found);
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
            }, methods.getAPIRequest(Constant.METHOD_RINGTONE, 0, "", "", ""));

            loadRingtone.execute();
        } else
        {
            errr_msg = getString(R.string.net_not_conn);
            setEmpty();
        }
    }


    private void setAdapter()
    {
        adapterRingtone = new AdapterRingtone(getActivity(), arrayList);
        recyclerView.setAdapter(adapterRingtone);
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

    @Override
    public void onDestroy()
    {
        if (adapterRingtone != null)
        {
            adapterRingtone.stopMediaPlayer();
        }
        super.onDestroy();
    }
}