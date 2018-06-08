package com.example.biabe.elderywatch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class HttpClient extends AsyncTask<String,Void,String> {

    private Context _mainContext;
    private String url;
    public  HttpClient(String url)
    {
        this.url = url;
    }

    protected String doInBackground(String... urls)

    {

        try
        {
            System.out.println(url);
            URL test = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) test.openConnection();
            InputStream is = connection.getInputStream();
            StringWriter sw = new StringWriter();
            String value = new java.util.Scanner(is).useDelimiter("\\A").next();
            Log.w("GOOGLE", value);

            return value;
        }

        catch (Exception ee) {

            System.out.println(ee);

        }

        return  "";

    }

}