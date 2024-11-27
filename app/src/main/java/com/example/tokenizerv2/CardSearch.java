package com.example.tokenizerv2;

import android.os.AsyncTask;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.BreakIterator;
import java.util.ArrayList;
public class CardSearch extends Fragment {
    private static RecyclerView cardRecyclerView;
    public CardSearch(RecyclerView cardRecyclerView){
        CardSearch.cardRecyclerView = cardRecyclerView;
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
                ArrayList<Card> cards = new ArrayList<>();

                StringBuilder displayText = new StringBuilder("com.example.tokenizerv2.Card names found:\n");
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject card = dataArray.getJSONObject(i);
                    String name = card.getString("name");
                    displayText.append("- ").append(name).append("\n");
                    String imageUrl = card.getJSONObject("image_uris").getString("large");
                    String description = card.getString("type_line");
                    String power = "N/A";
                    String toughness = "N/A";
                    if(description.toLowerCase().contains("creature")){
                        power = card.getString("power");
                        toughness = card.getString("toughness");
                    }
                    String rules = card.getString("oracle_text");

                    cards.add(new Card(name, imageUrl, description, rules, power, toughness));

                }

                CardAdapter cardAdapter = new CardAdapter(cards);
                RecyclerView recyclerView = cardRecyclerView;
                recyclerView.setLayoutManager(new LinearLayoutManager(cardRecyclerView.getContext()));
                recyclerView.setAdapter(cardAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }            }
    }
}
