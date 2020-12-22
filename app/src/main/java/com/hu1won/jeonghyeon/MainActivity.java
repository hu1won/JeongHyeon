package com.hu1won.jeonghyeon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "jsmon_api";

    private static final String TAG_JSON="result";
    private static final String site = "site";
    private static final String title = "title";
    private static final String category ="category";
    private static final String time ="time";
    private static final String title_url = "title_url";
    private static final String dir_link = "dir_link";

    private int num = 0;
    private boolean friendFlag = false;
    private String columm = "";

    private ArrayList<HashMap<String, String>> mArrayList;
    private ListView mlistView;
    String mJsonString;

    SwipeRefreshLayout mSwipeRefreshLayout;

    BottomNavigationView bottomNavigationView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mlistView = (ListView) findViewById(R.id.listView_main_list);
        mArrayList = new ArrayList<>();

        GetData task = new GetData();
        task.execute("http://49.246.37.135//jsmon_json_api/jsmon_api.php");

        mlistView.setOnScrollListener(onScrollListener);
        mSwipeRefreshLayout = findViewById(R.id.refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mArrayList.clear();
                if (columm == "jsmon_c19"){
                    GetData task = new GetData();
                    task.execute("http://49.246.37.135/jsmon_json_api/jsmon_c19_api.php");
                }
                else {
                    GetData task = new GetData();
                    task.execute("http://49.246.37.135/jsmon_json_api/jsmon_api.php");
                }
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });

        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setItemIconTintList(null); // 아이콘 색깔 on
        bottomNavigationView.setItemIconSize(90);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_1:
                        if (columm != "jsmon") {
                            mArrayList.clear();
                            GetData task = new GetData();
                            task.execute("http://49.246.37.135/jsmon_json_api/jsmon_api.php");
                            columm = "jsmon";
                            bottomNavigationView.setBackgroundColor(Color.parseColor("#a0daa9"));

                        }
                        return true;
                    case R.id.navigation_2:
                        Intent intent = new Intent(MainActivity.this, search_page.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_3:
                        if (columm != "jsmon_c19") {
                            mArrayList.clear();
                            GetData task = new GetData();
                            task.execute("http://49.246.37.135/jsmon_json_api/jsmon_c19_api.php");
                            columm = "jsmon_c19";
                            bottomNavigationView.setBackgroundColor(Color.parseColor("#FF6F61"));
                        }
                        return true;
                    case R.id.navigation_4:
                        bottomNavigationView.setBackgroundColor(Color.parseColor("#ffc107"));
                        Intent intent2 = new Intent(MainActivity.this, NotesListActivity.class);
                        startActivity(intent2);
                        return true;
                    case R.id.navigation_5:
                        Intent intent3 = new Intent(MainActivity.this, Introduce.class);
                        startActivity(intent3);
                        return true;
                }
                return false;
            }

        });
    }



    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mJsonString = result;
            showResult();
            num_result();

        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    private void num_result() {
        try {
            JSONObject jsonObnum = new JSONObject(mJsonString);
            num = jsonObnum.getInt("total") - 11;


        } catch (JSONException e) {

            Log.d(TAG, "num_result : ", e);
        }

    }

    private void showResult(){

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);


            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String tag_site = item.getString(site);
                String tag_title = item.getString(title);
                String tag_category = item.getString(category);
                String tag_time = item.getString(time);
                String tag_url = item.getString(title_url);
                String tag_dir_link = item.getString(dir_link);

                HashMap<String,String> hashMap = new HashMap<>();

                hashMap.put(site, tag_site);
                hashMap.put(title, tag_title);
                hashMap.put(category, tag_category);
                hashMap.put(time, tag_time);

                mArrayList.add(hashMap);
            }

            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, mArrayList, R.layout.item_list,
                    new String[]{site, title, category, time},
                    new int[]{R.id.textView_list_site, R.id.textView_list_title, R.id.textView_list_category, R.id.textView_list_time}
            );

            //리스트가 새로 추가됬을 시에 스크롤을 보이고있는 리스트의 맨 첫번째로 맞춰줌(스크롤이 최상단으로 움직이는거 방지)
            int first = mlistView.getFirstVisiblePosition();

            mlistView.setAdapter(adapter);
            mlistView.setSelection(first);

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }

    }
    private AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener(){

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && friendFlag) {
                if (columm == "jsmon_c19") {
                    GetData task = new GetData();
                    task.execute("http://49.246.37.135/jsmon_json_api/jsmon_c19_scroll_api.php?num=" + num);
                    num = num - 5;
                }
                else {
                    GetData task = new GetData();
                    task.execute("http://49.246.37.135/jsmon_json_api/jsmon_scroll_api.php?num=" + num);
                    num = num - 5;
                }
            }

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            friendFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
        }
    };

}