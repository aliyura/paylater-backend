package com.syrol.paylater.util;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class AppHttpClient<T> {

    private  HttpURLConnection conn;
    private String result = "";


    public AppHttpClient(String url){
        try {
           URL  endpoint = new URL(url);
           conn = (HttpURLConnection) endpoint.openConnection();
           conn.setDoOutput(true);
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    public void   addHeader(String key, String value){
        conn.setRequestProperty(key, value);
    }

    public String getJsonResponse() {
        if(result!=null) {
            System.out.println("Converting: " + result);
            String jsonString = result.replace("jsonp (", "").replaceAll("\\)$", "").trim();
            System.out.println("Prepared String: " + jsonString);
            return jsonString;
        }else{
            return  null;
        }
    }

    public String  post(T body) {
        try {
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(new Gson().toJson(body).getBytes());
            os.flush();

            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + conn.getURL());
            System.out.println("Response Code : " + responseCode);
            System.out.println("Request:");
            System.out.println(body);

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                result = response.append(line).toString();
            }
            System.out.println("Response :" + result);
            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String  post() {
        try {
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.flush();

            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + conn.getURL());
            System.out.println("Response Code : " + responseCode);
            System.out.println("Request:");

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                result = response.append(line).toString();
            }
            System.out.println("Response :" + result);
            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String  get() {
        String result = "";
        try {

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            System.out.println("\nSending 'GET' request to URL : " + conn.getURL());
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                result = response.append(line).toString();
            }
            System.out.println("Response :" + result);
            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
