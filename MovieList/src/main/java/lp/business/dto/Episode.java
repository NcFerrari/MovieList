package lp.business.dto;

import javafx.scene.control.CheckBox;
//import lombok.Data;

import java.util.ArrayList;
import java.util.List;

//@Data
public class Episode extends CheckBox {

    private String title;
    private boolean directory;
    private List<Episode> episodes = new ArrayList<>();

}
