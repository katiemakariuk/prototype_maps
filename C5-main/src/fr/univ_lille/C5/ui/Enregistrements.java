package fr.univ_lille.C5.ui;

import fr.univ_lille.C5.Historique;
import fr.univ_lille.C5.TypeCout;
import fr.univ_lille.C5.VoyageEffectue;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class Enregistrements extends Application {
    public void start(Stage stage) {
        afficher(stage, new Historique());
    }

    public static void afficher(Stage stage, Historique historique) {
        stage.setMinWidth(350);
        stage.setMinHeight(350);

        VBox root = new VBox(8);
        root.setStyle("-fx-padding: 15;");
        root.getChildren().add(new Label(historique.taille() + " voyage(s) effectué(s)"));
        root.getChildren().add(new Label(historique.analysePersonnalisee()));

        TabPane evolutions = new TabPane();
        evolutions.getTabs().add(creerOnglet("Prix", TypeCout.PRIX, historique));
        evolutions.getTabs().add(creerOnglet("CO2", TypeCout.CO2, historique));
        evolutions.getTabs().add(creerOnglet("Temps", TypeCout.TEMPS, historique));

        ListView<String> voyagesRecents = new ListView<>();
        for (VoyageEffectue voyage : historique.voyagesRecents(10)) {
            voyagesRecents.getItems().add(voyage.toString());
        }

        Button vider = new Button("Vider l'historique");
        vider.setOnAction(event -> {
            try {
                historique.vider();
                afficher(stage, historique);
            } catch (IOException e) {
                vider.setText("Impossible de vider l'historique");
            }
        });

        root.getChildren().addAll(evolutions, new Label("Voyages récents"), voyagesRecents, vider);
        stage.setScene(new Scene(root, 700, 550));
        stage.setTitle("Évolution de mes voyages");
        stage.show();
    }

    private static Tab creerOnglet(String titre, TypeCout type, Historique historique) {
        VBox contenu = new VBox(6);
        contenu.setStyle("-fx-padding: 10;");
        contenu.getChildren().add(new Label(historique.moyenne(type)));
        ListView<String> evolution = new ListView<>();
        evolution.getItems().addAll(historique.evolution(type));
        contenu.getChildren().add(evolution);
        Tab onglet = new Tab(titre, contenu);
        onglet.setClosable(false);
        return onglet;
    }

    public static void main(String[] args) {
        Application.launch();
    }
}
