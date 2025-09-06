package eve.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Eve using FXML.
 */
public class Main extends Application {

    private eve.Eve eve = new eve.Eve();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setEve(eve);
            stage.setTitle("Eve");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("MainWindow.fxml loading error");
        }
    }
}
