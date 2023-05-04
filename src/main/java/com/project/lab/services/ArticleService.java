package com.project.lab.services;

import com.project.lab.models.NytResponse;
import com.project.lab.models.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleService {
    @Value("${api_key}")
    private String apikey;

    @Value("${nytUrl}")
    private String nytUrl;

    private String query = "economy";

    @Autowired
    RestTemplate restTemplate;

    public List<Article> getEconomyNews() {
        NytResponse response = restTemplate.getForObject(nytUrl + "q=" + query + "&api-key=" + apikey, NytResponse.class);
        List<Article> results = new ArrayList<>();
        if (response != null && response.getStatus().equals("OK")) {
            return response.getResponse().getDocs();
        } else {
            return results;
        }
    }
}
