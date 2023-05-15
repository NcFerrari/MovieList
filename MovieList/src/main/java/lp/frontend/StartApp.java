package lp.frontend;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lp.business.dto.Episode;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartApp extends Application {

    private static Episode importedEpisode;

    public static Episode getImportedEpisode() {
        return importedEpisode;
    }

    public static void setImportedEpisode(Episode importedEpisode) {
        StartApp.importedEpisode = importedEpisode;
    }

    private final Toolkit tool = Toolkit.getDefaultToolkit();
    private final Dimension src = tool.getScreenSize();
    private final List<Button> buttonList = new ArrayList<>();
    private final Map<String, List<CheckBox>> episodeMap = new HashMap<>();

    private BorderPane mainPane;
    private FlowPane menuPane;
    private ScrollPane choiceScrollPane;
    private VBox choicePane;

    public void start(Stage stage) {
        mainPane = new BorderPane();
        addStyle(mainPane, StyleClasses.PANE);
        Scene scene = new Scene(mainPane, src.width / 2, src.height / 2);
        stage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("/css/fx-component.css").toExternalForm());
////        primaryStage.setMaximized(true);
        stage.setTitle("Seznam filmů");
        stage.show();

        setMenu();
        setCurrentSelectedItemPane();

        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            resize();
            fillChoicePane(null);
        });
        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            resize();
            fillChoicePane(null);
        });
        stage.maximizedProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                resize();
                fillChoicePane(null);
            });
        });
    }

    private void setMenu() {
        menuPane = new FlowPane();
        addStyle(menuPane, StyleClasses.MENU);
        mainPane.setTop(menuPane);
        if (getImportedEpisode().getEpisodes().isEmpty()) {
            return;
        }
        getImportedEpisode().getEpisodes().forEach(episode -> {
            Button menuButton = new Button(episode.getTitle());
            addStyle(menuButton, StyleClasses.MENU_BUTTON);
            buttonList.add(menuButton);
            menuPane.setMargin(menuButton, new Insets(10));
            menuPane.getChildren().add(menuButton);

            menuButton.setOnMouseClicked(evt -> {
                setSelectedButton(menuButton);
                if (!episodeMap.containsKey(episode.getTitle())) {
                    List<CheckBox> newCheckBoxList = new ArrayList<>();
                    episode.getEpisodes().stream().forEach(subEpisode -> {
                        CheckBox checkBox = new CheckBox(subEpisode.getTitle());
                        addStyle(checkBox, StyleClasses.CHECKBOX);
                        Tooltip checkBoxTooltip = new Tooltip(subEpisode.getTitle());
                        checkBoxTooltip.setFont(new Font("Arial", 16));
                        checkBox.setTooltip(checkBoxTooltip);
                        newCheckBoxList.add(checkBox);
                    });
                    episodeMap.put(episode.getTitle(), newCheckBoxList);
                }
                resize();
                fillChoicePane(episode.getTitle());
            });
        });
    }

    private void fillChoicePane(String title) {
        choicePane.getChildren().clear();
        List<CheckBox> currentCheckBoxList = null;
        if (episodeMap.containsKey(title)) {
            currentCheckBoxList = episodeMap.get(title);
            currentCheckBoxList.stream().forEach(checkBox -> {
                checkBox.setPrefWidth(mainPane.getWidth() / 2 - 20);
                choicePane.getChildren().add(checkBox);
            });
        }
    }

    private void resize() {
        choiceScrollPane.setPrefSize(mainPane.getWidth() / 2, mainPane.getHeight() - menuPane.getHeight());
        choicePane.setPrefSize(mainPane.getWidth() / 2, mainPane.getHeight() - menuPane.getHeight());
    }

    private void setSelectedButton(Button selectedButton) {
        for (Button button : buttonList) {
            if (button.getStyleClass().contains(StyleClasses.SELECTED.getClassName())) {
                button.getStyleClass().remove(StyleClasses.SELECTED.getClassName());
                break;
            }
        }
        addStyle(selectedButton, StyleClasses.SELECTED);
    }

    private void setCurrentSelectedItemPane() {
        Pane paneForScrolling = new Pane();
        mainPane.setCenter(paneForScrolling);
        choiceScrollPane = new ScrollPane();
        paneForScrolling.getChildren().add(choiceScrollPane);
        choicePane = new VBox();
        choicePane.setStyle("-fx-background-color: #7e6969");
        choicePane.setMinWidth(mainPane.getWidth() / 2);
        choiceScrollPane.setContent(choicePane);
        choiceScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        resize();
    }

    private void addStyle(Node node, StyleClasses style) {
        node.getStyleClass().add(style.getClassName());
    }
}
