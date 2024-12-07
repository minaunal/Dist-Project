package com.geziblog.geziblog.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GooglePlacesResponse {

    private List<GooglePlace> results;  // API'den dönen sonuçlar listesi

    // Getter ve Setter metodları
    public List<GooglePlace> getResults() {
        return results;
    }

    public void setResults(List<GooglePlace> results) {
        this.results = results;
    }
}

