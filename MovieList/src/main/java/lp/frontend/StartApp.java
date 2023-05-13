package lp.frontend;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class StartApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        TextInputDialog tid = new TextInputDialog();
        tid.setTitle("Tests");
        tid.setHeaderText("test");
        tid.showAndWait();
        Pane pane = new Pane();
        Scene scene = new Scene(pane, 500, 500);
        stage.setScene(scene);
        stage.show();
    }
}
