package com.leanderziehm.androidjava;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProcessTextActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Receive selected text
        Intent intent = getIntent();
        if (Intent.ACTION_PROCESS_TEXT.equals(intent.getAction())) {
            CharSequence selectedText = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);

            if (selectedText != null) {
                // Send text to API
                new SendTextTask().execute(selectedText.toString());
            } else {
                Toast.makeText(this, "No text selected", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            finish();
        }
    }

    private class SendTextTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            String text = strings[0];
            try {
                URL url = new URL("https://tracker-api.leanderziehm.com/texts");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setDoOutput(true);

                // Create JSON body
                JSONObject json = new JSONObject();
                json.put("text", text);

                // Write JSON to request body
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = json.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                conn.disconnect();
                return responseCode >= 200 && responseCode < 300;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(ProcessTextActivity.this, "Text sent successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProcessTextActivity.this, "Failed to send text", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }
}
