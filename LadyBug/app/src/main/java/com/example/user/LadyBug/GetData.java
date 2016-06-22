package com.example.user.LadyBug;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class GetData {

    String myJSON;

    public static final String TAG_RESULTS = "result";
    public static final String TAG_LAST = "last";
    public static final String TAG_CURRENT = "current";
    public static final String TAG_DATE = "date";
    public static final String TAG_SEAT= "seat";
    public static final String TAG_DEST= "dest";


    public boolean check= true;
    JSONArray data_from_db = null;
    ArrayList<HashMap<String, String>> dataList;


    protected void showList() {
        dataList = new ArrayList<HashMap<String, String>>();
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            data_from_db = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < data_from_db.length(); i++) {
                JSONObject c = data_from_db.getJSONObject(i);
                String last = c.getString(TAG_LAST);
                String current = c.getString(TAG_CURRENT);
                String date = c.getString(TAG_DATE);
                String seat = c.getString(TAG_SEAT);
                String dest = c.getString(TAG_DEST);

                HashMap<String, String> persons = new HashMap<String, String>();
                persons.put(TAG_LAST, last);
                persons.put(TAG_CURRENT, current);
                persons.put(TAG_DATE, date);
                persons.put(TAG_SEAT,seat);
                persons.put(TAG_DEST,dest);

                dataList.add(persons);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        check = false;
/*
        for (int i = 0; i<dataList.size(); i++) {
            //System.out.println(i + " : " + dataList.get(i).getClass()); //Class 알아볼 때
            System.out.println("check" + dataList.size());
            System.out.println("check"+i + " : " + dataList.get(i).get(TAG_LAST) +dataList.get(i).get(TAG_CURRENT) + dataList.get(i).get(TAG_DATE) );
        }
*/
    }
    //쓰레드를 통해 서버에서 데이터를 가져옴
    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    myJSON = sb.toString().trim();
                    showList();
                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }
            }
            @Override
            protected void onPostExecute(String result) {

            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    public ArrayList getList() {
        return dataList;
    }

}
