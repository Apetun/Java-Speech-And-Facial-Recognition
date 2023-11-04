package com.example.oops;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class VideoApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainStage.fxml"));
        Parent root = fxmlLoader.load();
        stage.setTitle("Camera Capture");
        stage.setScene(new Scene(root,700,600));
        stage.show();
    }

    public static void main(String[] args) {
        nu.pattern.OpenCV.loadShared();
        launch(args);
    }
}