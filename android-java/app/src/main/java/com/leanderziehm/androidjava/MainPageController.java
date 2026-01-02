package com.leanderziehm.androidjava;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainPageController {

    private final Context context;
    private final View rootView;

    private EditText inputText;
    private Button sendButton;
    private RecyclerView recyclerView;
    private TextAdapter adapter;
    private ArrayList<String> textList = new ArrayList<>();

    public MainPageController(Context context, View rootView) {
        this.context = context;
        this.rootView = rootView;
    }

    public void init() {
        // Bind views
        inputText = rootView.findViewById(R.id.inputText);
        sendButton = rootView.findViewById(R.id.sendButton);
        recyclerView = rootView.findViewById(R.id.textList);

        // Setup RecyclerView
        adapter = new TextAdapter(textList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        // Load existing texts
        new Thread(this::loadTexts).start();

        // Send button
        sendButton.setOnClickListener(v -> {
            String text = inputText.getText().toString().trim();
            if (!text.isEmpty()) {
                new Thread(() -> sendText(text)).start();
                inputText.setText("");
            } else {
                Toast.makeText(context, "Enter some text", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTexts() {
        try {
            URL url = new URL("https://tracker-api.leanderziehm.com/texts?per_page=30");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "application/json");
//            conn.setRequestProperty();
//            json.put("per_page", 30);

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();

            JSONArray jsonArray = new JSONArray(sb.toString());
            ArrayList<String> results = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                results.add(obj.getString("text"));
            }

            conn.disconnect();

            // Update UI on main thread
            ((MainActivity) context).runOnUiThread(() -> {
                textList.clear();
                textList.addAll(results);
                adapter.updateData(textList);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendText(String text) {
        try {
            URL url = new URL("https://tracker-api.leanderziehm.com/texts");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("text", text);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.toString().getBytes("utf-8"));
            }

            int code = conn.getResponseCode();
            conn.disconnect();

            ((MainActivity) context).runOnUiThread(() -> {
                if (code >= 200 && code < 300) {
                    Toast.makeText(context, "Text sent!", Toast.LENGTH_SHORT).show();
                    new Thread(this::loadTexts).start(); // Refresh list
                } else {
                    Toast.makeText(context, "Failed to send text", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
