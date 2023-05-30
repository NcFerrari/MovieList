package lp.business.dto;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class Episode {

    private String title;
    private boolean selected;
    private Map<String, Episode> episodes = new LinkedHashMap<>();

    public Episode(String title) {
        this.title = title;
    }
}
