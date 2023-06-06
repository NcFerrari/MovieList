package lp.business.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class Episode {

    private String title;
    private boolean selected;
    private long size;
    private Map<String, Episode> subEpisodes = new LinkedHashMap<>();

    public Episode(String title) {
        this.title = title;
    }
}
