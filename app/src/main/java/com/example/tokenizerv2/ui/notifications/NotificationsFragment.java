package com.example.tokenizerv2.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tokenizerv2.CardAdapter;
import com.example.tokenizerv2.DownloadedCardAdapter;
import com.example.tokenizerv2.R;
import com.example.tokenizerv2.SharedViewModel;
import com.example.tokenizerv2.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {
    private RecyclerView recyclerView;
    private DownloadedCardAdapter adapter;
    private SharedViewModel sharedViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getDownloadedCards().observe(getViewLifecycleOwner(), cards -> {
            DownloadedCardAdapter adapter = new DownloadedCardAdapter(cards);
            recyclerView.setAdapter(adapter);
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}