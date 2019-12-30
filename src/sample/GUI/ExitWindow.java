package sample.GUI;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;

public class ExitWindow {

    private static boolean answer;

    public static boolean display(String title, String message) {
        Stage exit = new Stage();
        exit.setMinHeight(200.0);
        exit.setMaxWidth(500.0);
        exit.setTitle(title);
        exit.initModality(Modality.APPLICATION_MODAL);

        Label exitMessage = new Label(message);
        exitMessage.setFont(Font.font("Arial", 16));

        Button yes = new Button("Да");
        yes.setPrefSize(80, 30);

        Button no = new Button("Отмена");
        no.setPrefSize(80, 30);

        yes.setOnAction(event -> {
            answer = true;
            exit.close();
        });

        no.setOnAction(event -> {
            answer = false;
            exit.close();
        });

        VBox layout = new VBox();
        HBox layout2 = new HBox();

        layout2.setSpacing(20);
        layout2.getChildren().addAll(yes, no);
        layout2.setAlignment(Pos.CENTER);

        layout.setSpacing(40);
        layout.getChildren().addAll(exitMessage, layout2);
        layout.setAlignment(Pos.CENTER);

        Scene confirmScene = new Scene(layout);
        exit.setScene(confirmScene);
        exit.showAndWait();

        return answer;
    }
}

