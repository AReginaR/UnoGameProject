package sample.GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.controller.MainScreenController;

public class MainScreen extends Application{
    private static Stage chat;

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/sample/fxml/MainScreenWindow.fxml"));
        AnchorPane root = loader.load();

        primaryStage.setTitle("UNO!");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/sample/fxml/cards/icon.png")));
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.show();

        MainScreenController cont = loader.getController();
        cont.setAll(root, primaryStage);

        primaryStage.setOnCloseRequest(event -> {
            if (ExitWindow.display("Завершение программы", "Вы действительно хотите выйти из программы?")) {
                primaryStage.close();
            }
            event.consume();
        });
    }

    public static void main(String[] args) {

        launch(args);
    }

}