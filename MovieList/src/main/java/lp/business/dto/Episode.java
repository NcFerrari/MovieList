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
    private Map<String, Episode> episodes = new LinkedHashMap<>();

    public Episode(String title) {
        this.title = title;
    }
}
