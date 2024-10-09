package com.example.tokenizerv2.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tokenizerv2.CardSearch;
import com.example.tokenizerv2.R;
import com.example.tokenizerv2.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    private EditText searchEditText;
    private Button searchButton;
    private TextView resultTextView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        searchEditText = binding.searchEditText;
        searchButton = binding.searchButton;
        resultTextView = binding.resultTextView;

        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString();
            CardSearch cs = new CardSearch(resultTextView);

            new CardSearch.FetchCardDataTask().execute(query);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}