package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {
    @FXML
    private Button start;

    @FXML
    void open(ActionEvent event) throws IOException {

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = (Parent)loader.load();
        Interviewee controller = (Interviewee)loader.getController();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(KeywordsAsync.class.getResource("Light.css").toExternalForm());
        stage.hide();
        stage.setScene(scene);
        stage.setTitle("editor");
        controller.setStageAndSetupListeners(stage);
        stage.show();
    }
}
