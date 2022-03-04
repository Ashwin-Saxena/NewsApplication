package com.ashwinsaxena.newsapplication;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsFragment extends Fragment implements NewsAdapter.ItemClickListener {

    private ArrayList<DataModel> dataHolderList;
    private DAO dao;
    private MainActivity sActivity;

    public NewsFragment() {
        // Required empty public constructor
    }

    public static NewsFragment newInstance() {
        return new NewsFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sActivity = (MainActivity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Creating the view with XML Resource fragment_news
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppDatabase databaseClass = AppDatabase.getInstance(sActivity);
        dao = databaseClass.DAO();
        if (savedInstanceState != null) {
            dataHolderList = savedInstanceState.getParcelableArrayList(Constants.DATA_LIST_KEY);
            displayNews();
        } else {
            String currentCountry = sActivity.getCurrentCountry();
            fetchNews(currentCountry);
        }
    }

    private void displayNews() {
        View view = getView();
        if (view != null) {
            RecyclerView recyclerView = view.findViewById(R.id.news_list);
            recyclerView.setLayoutManager(new LinearLayoutManager(sActivity));
            ProgressBar progressBar = view.findViewById(R.id.progress_circular_bar);

            //Data Flow to here should never be NULL
            //Called From 3 places where list can be empty,filled but NEVER be NULL
            if (dataHolderList.size() == 0) {
                TextView txtView = view.findViewById(R.id.status);
                txtView.setText(R.string.no_news_available);
            }
            //Updating the existing Recycler View either to filling newly fetched news or
            // making it empty in case of No News in any possible scenario
            NewsAdapter adapter = new NewsAdapter(dataHolderList, sActivity, pos -> {
                if (sActivity == null) return;
                sActivity.showDetailNews(dataHolderList, pos);
            });
            recyclerView.setAdapter(adapter);
            progressBar.setVisibility(View.GONE);
        }
    }

    //Method for Fetching News for the given country
    private void fetchNews(String country) {
        RetrofitClient.getApiInterface().getNews(country, 20, NewsAPI.KEY)
                .enqueue(new Callback<NewsStructure>() {
                    //If the response is successful we will save that news to RoomDB through
                    // saveNews() Method and display that news to recycler view using displayNews() Method
                    @Override
                    public void onResponse(@NonNull Call<NewsStructure> call,
                                           @NonNull Response<NewsStructure> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            //dataHolderList will never be empty as
                            // response with 0 news will still return empty ArrayList
                            dataHolderList = new ArrayList<>(response.body().getArticles());
                            displayNews();
                            saveNews();
                        }
                    }
                    //In case of no internet or some error while fetching live news,
                    // it will try to show previously loaded news
                    @Override
                    public void onFailure(@NonNull Call<NewsStructure> call, @NonNull Throwable t) {
                        showSavedNews();
                    }
                });
    }

    //Method for showing previously loaded news
    private void showSavedNews() {
        new Thread(() -> {
            dataHolderList = (ArrayList<DataModel>) dao.getNews();
            //Post successful retrievement of News From RoomDB
            new Handler(Looper.getMainLooper()).post(this::displayNews);
        }).start();
    }

    //Method for saving News to local DB
    private void saveNews() {
        new Thread(() -> {
            DataModel dataModel;
            for (int i = 0; i < dataHolderList.size(); i++) {
                dataModel = dataHolderList.get(i);
                try {
                    String curUrl = dataModel.getUrlToImage();
                    dataModel.setUrlToImage(convertInternetUrlToLocalUrl(curUrl));
                } catch (Exception e) {
                    dataModel.setUrlToImage(null);
                }
            }
            //First Delete all the existing news from RoomDb
            dao.deleteAll();
            //Now insert the fetched news to RoomDB
            dao.insertNews(dataHolderList);
        }).start();
    }

    //Method for saving the images to Local Storage and creating their url's
    private String convertInternetUrlToLocalUrl(String urlToImage) throws Exception {
        Bitmap bitmap = Glide.with(sActivity).asBitmap().load(urlToImage).submit().get();
        File filePath = sActivity.getFilesDir();
        File directory = new File(filePath.getAbsolutePath() + File.separator +
                R.string.news_app + File.separator);
        if (!directory.exists() && !directory.mkdirs()) {
            Toast.makeText(sActivity, R.string.unable_to_save_images, Toast.LENGTH_SHORT).show();
            throw new IOException(getString(R.string.error_creating_directory));
        }
        File file = new File(directory, System.currentTimeMillis() + ".jpeg");
        OutputStream outputStream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        String ans = file.getAbsolutePath();
        outputStream.flush();
        outputStream.close();
        return ans;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(Constants.DATA_LIST_KEY, dataHolderList);
        super.onSaveInstanceState(outState);
    }

    //When new country is selected then this method is called from MainActivity
    public void updateNews(String newCountry) {
        fetchNews(newCountry);
    }

    @Override
    public void showDetailNews(int position) {
        sActivity.showDetailNews(dataHolderList, position);
    }
}