package fr.univ_lille.C5.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class Profil extends Application {
    public void start(Stage stage) throws FileNotFoundException {
        VBox root = new VBox();
        Scene sc = new Scene(root);
        stage.setScene(sc);

        Circle pfp = new Circle();
        pfp.setStroke(Color.BLACK);
        pfp.setRadius(50);
        Image pfpImg = new Image(new FileInputStream("res/img/kermit-the-frog.jpg"));
        pfp.setFill(new ImagePattern(pfpImg));

        Label nom = new Label("Username");
        Button save = new Button("Enregistrements");
        Button settings = new Button("Paramètres");
        Button preference = new Button("Préférences");

        root.getChildren().addAll(pfp,nom,save,settings,preference);

        stage.show();
    }

    public static void main(String[] args){
        Application.launch();
    }
}