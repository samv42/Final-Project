package com.project.lab.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Headline {
    String main;
    String kicker;
    @JsonProperty("content_kicker")
    String contentKicker;
    @JsonProperty("print_headline")
    String printHeadline;
    String name;
    String seo;
    String sub;
}
