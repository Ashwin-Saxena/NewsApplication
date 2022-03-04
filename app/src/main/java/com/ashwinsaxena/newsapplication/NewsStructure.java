package com.ashwinsaxena.newsapplication;

import java.util.ArrayList;

public class NewsStructure {

    private String status;
    private String totalResults;
    private ArrayList<DataModel> articles;

    public NewsStructure(String status, String totalResults, ArrayList<DataModel> articles) {
        this.status = status;
        this.totalResults = totalResults;
        this.articles = articles;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(String totalResults) {
        this.totalResults = totalResults;
    }

    public ArrayList<DataModel> getArticles() {
        return articles;
    }

    public void setArticles(ArrayList<DataModel> articles) {
        this.articles = articles;
    }
}
