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
    private Headline headline;
    @JsonProperty("web_url")
    private String webUrl;
    private String url;
    private String snippet;
    @JsonProperty("news_desk")
    private String newsDesk;
    @JsonProperty("abstract")
    private String summary;

    private List<Media> media;
    private String imageUrl;
}
