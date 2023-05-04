package com.project.lab.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Article {
    private Long id;
    //private String headline;
    @JsonProperty("web_url")
    private String webUrl;
    private String url;
    //@JsonProperty("lead_paragraph")
    //private String leadParagraph;
    private String snippet;
    @JsonProperty("news_desk")
    private String newsDesk;
    //@JsonProperty("pub_date")
    //private String date;
    @JsonProperty("abstract")
    private String summary;

    private List<Media> media;
    private String imageUrl;
}
