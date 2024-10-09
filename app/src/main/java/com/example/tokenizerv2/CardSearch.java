package com.example.tokenizerv2;

import android.os.AsyncTask;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.BreakIterator;

public class CardSearch extends Fragment {
    private static TextView resultTextView;
    public CardSearch(TextView resultTextView){
        CardSearch.resultTextView = resultTextView;
    }
    public static class FetchCardDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String query = params[0];
            String urlString = "https://api.scryfall.com/cards/search?q=" + query;

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    response.append(line);
                }
                reader.close();

                return response.toString();
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String jsonString) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray dataArray = jsonObject.getJSONArray("data");

                StringBuilder displayText = new StringBuilder("Card names found:\n");
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject card = dataArray.getJSONObject(i);
                    String name = card.getString("name");
                    displayText.append("- ").append(name).append("\n");
                }

                resultTextView.setText(displayText.toString());
            } catch (JSONException e) {
                resultTextView.setText("Error parsing JSON: " + e.getMessage());
            }            }
    }
}
