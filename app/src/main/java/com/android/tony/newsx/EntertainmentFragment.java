package com.android.tony.newsx;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;


public class EntertainmentFragment extends Fragment {
    private ProgressBar progressBar;
    private DownloadTask downloadTask;
    private NewsAdapter newsAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<NewsDatabase> newsDatabaseList;
    private RecyclerView recyclerView;
    private int loopInt=0,currItems,totalItems,scrollOutItems,condition=0,articleSize=0;
    private Boolean iscroll=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_for_you, container, false);
        recyclerView = v.findViewById(R.id.forYouRecyclerView);
        progressBar = v.findViewById(R.id.forYouProgressBar);
        newsDatabaseList = new ArrayList<>();
        newsAdapter = new NewsAdapter(newsDatabaseList);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(newsAdapter);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.addOnItemTouchListener(new RecyclerViewClickListener(getApplicationContext(), recyclerView, new RecyclerViewClickListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                NewsDatabase newsDatabase = newsDatabaseList.get(position);
                Intent intent = new Intent(getApplicationContext(),NewsDetailsActivity.class);
                intent.putExtra("news_url",newsDatabase.getUriUrl());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int childPosition) {

            }
        }));
        loopInt =0;
        downloadTask = new DownloadTask();
        try {
            downloadTask.execute("https://newsapi.org/v2/everything?q=entertainment&apiKey=6f935b27a3fa4a20b9a52aeef6f07add");
        } catch (Exception e) {
            Log.i("latest", "" + e);
        }




        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                {
                    iscroll = true;
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currItems = linearLayoutManager.getChildCount();
                totalItems = linearLayoutManager.getItemCount();
                scrollOutItems = linearLayoutManager.findFirstVisibleItemPosition();

                if(iscroll && (currItems+scrollOutItems) == totalItems && condition<articleSize)
                {
                    loopInt = totalItems;
                    downloadTask = new DownloadTask();
                    try {
                        downloadTask.execute("https://newsapi.org/v2/everything?q=entertainment&apiKey=6f935b27a3fa4a20b9a52aeef6f07add");
                    } catch (Exception e) {
                        Log.i("json Error", "" + e);
                    }
                }
                else
                {
                    recyclerView.setNestedScrollingEnabled(false);
                    recyclerView.stopScroll();
                    iscroll = false;
                }

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            newsAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                String result = "";
                URL url = new URL(urls[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = inputStreamReader.read();
                }

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("articles");
                if (condition == 0) condition = 10;
                else if (condition < jsonArray.length()) condition += 10;
                else condition = jsonArray.length();
                for (int i = loopInt; i < condition; i++) {
                    String[] inp = jsonArray.getJSONObject(i).get("source").toString().replace("}", "").split(":");
                    String[] res = inp[2].replaceAll("\"", "").split("\\.");
                    newsDatabaseList.add(new NewsDatabase(Uri.parse(jsonArray.getJSONObject(i).getString("urlToImage")), jsonArray.getJSONObject(i).getString("title"), res[0], jsonArray.getJSONObject(i).getString("publishedAt").replace("T", " ").replace("Z", ""),Uri.parse(jsonArray.getJSONObject(i).getString("url"))));
                }
                loopInt += 10;
                Log.i("json", jsonArray.length() + " " + loopInt);
                articleSize = jsonArray.length();
            } catch (Exception e) {
                Log.i("json error", "" + e.getLocalizedMessage());
            }
            return null;
        }
    }
}


