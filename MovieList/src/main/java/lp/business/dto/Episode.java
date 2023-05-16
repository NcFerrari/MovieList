package lp.business.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Episode {

    private String title;
    private List<Episode> episodes = new ArrayList<>();

}
