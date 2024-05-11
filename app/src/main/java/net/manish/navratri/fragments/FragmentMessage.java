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

import net.manish.navratri.adapter.AdapterMessage;
import net.manish.navratri.asyncTask.LoadMessage;
import net.manish.navratri.R;
import net.manish.navratri.interfaces.MessageListener;
import net.manish.navratri.item.ItemMessage;
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

public class FragmentMessage extends Fragment
{

    private Methods methods;
    private RecyclerView recyclerView;
    private AdapterMessage adapterMessage;
    private ArrayList<ItemMessage> arrayList;
    private CircularProgressBar progressBar;

    private TextView textView_empty;
    private AppCompatButton button_try;
    private LinearLayout ll_empty;
    private String errr_msg;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_messsage, container, false);

        methods = new Methods(getActivity());

        arrayList = new ArrayList<>();

        ll_empty = rootView.findViewById(R.id.ll_empty);
        textView_empty = rootView.findViewById(R.id.textView_empty_msg);
        button_try = rootView.findViewById(R.id.button_empty_try);
        errr_msg = getString(R.string.no_message_found);

        progressBar = rootView.findViewById(R.id.pb_message);
        recyclerView = rootView.findViewById(R.id.rv_message);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        button_try.setOnClickListener(v -> loadMessage());

        loadMessage();

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

        // inside inflater we are inflating our menu file.
        inflater.inflate(R.menu.search, menu);

        // below line is to get our menu item.
        MenuItem searchItem = menu.findItem(R.id.actionSearch);

        // getting search view of our item.
        SearchView searchView = (SearchView) searchItem.getActionView();

        // below line is to call set on query text listener method.
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
        ArrayList<ItemMessage> filteredlist = new ArrayList<ItemMessage>();
        for (ItemMessage item : arrayList)
        {
            if (item.getNumber().toLowerCase().contains(text.toLowerCase()))
            {
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty())
        {
            Toast.makeText(getActivity(), "No Data Found..", Toast.LENGTH_SHORT).show();
        } else
        {
            adapterMessage.filterList(filteredlist);
        }
    }

    private void loadMessage()
    {
        if (methods.isNetworkAvailable())
        {
            LoadMessage loadMessage = new LoadMessage(new MessageListener()
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
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemMessage> arrayListMessage)
                {
                    if (getActivity() != null)
                    {

                        if (success.equals("1"))
                        {
                            if (!verifyStatus.equals("-1"))
                            {
                                arrayList.addAll(arrayListMessage);
                                errr_msg = getString(R.string.no_message_found);
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
            }, methods.getAPIRequest(Constant.METHOD_QUOTES, 0, "", "", ""));
            loadMessage.execute();
        } else
        {
            errr_msg = getString(R.string.net_not_conn);
            setEmpty();
        }
    }

    private void setAdapter()
    {
        adapterMessage = new AdapterMessage(getActivity(), arrayList);
        recyclerView.setAdapter(adapterMessage);
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