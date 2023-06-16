package lp.business.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"title", "selected", "size", "subEpisodes"})
public class Episode {

    private final Map<String, Episode> subEpisodes = new LinkedHashMap<>();

    private String title;
    private boolean selected;
    private long size;

    public Episode(String title) {
        this.title = title;
    }
}
