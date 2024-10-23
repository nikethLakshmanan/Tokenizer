package com.example.tokenizerv2;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<List<Card>> downloadedCards = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Card>> getDownloadedCards() {
        return downloadedCards;
    }

    public void addCard(Card card) {
        List<Card> currentCards = downloadedCards.getValue();
        Boolean reused = false;
        for(int i = 0; i<currentCards.size();i++){
            if(currentCards.get(i).getName().equals(card.getName())){
                reused = true;
            }
        }

        if (currentCards != null && reused == false) {
            currentCards.add(card);
            downloadedCards.postValue(currentCards);
        }
    }
}