package fr.univ_lille.C5.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class Parametres {
    public static boolean mode_abrégé = true;
    public static boolean thème_sombre = false;
    public static Short tailleAffichage = null; // On a mis short prcque la limite est pas censée être immense
    public static Stage fenêtreParamètres = null;

    public static void chargerTheme(String theme) {
        try {
            Application.setUserAgentStylesheet(
                new File("res/css/" + theme + (thème_sombre ? "-dark" : "-light")  + ".css").toURI().toURL().toExternalForm()
            );
        } catch (MalformedURLException e) {}
    }

    public static void start(Stage stage) {
        fenêtreParamètres = new Stage();

        FXMLLoader loader = new FXMLLoader();
        try {
            URL fxmlFileUrl = Parametres.class.getResource("parametres.fxml");
            loader.setLocation(fxmlFileUrl);
            Parent root = loader.load();
            final ListView<String> listeDesThèmes = (ListView<String>) root.lookup("#liste_des_themes");
            final TextField champMaxLignes = ((TextField) root.lookup("#champ_max_lignes"));
            final CheckBox modeSombre = (CheckBox) root.lookup("#mode_sombre");
            final CheckBox modeAbrégé = ((CheckBox) root.lookup("#mode_abrégé"));
            champMaxLignes.setText(tailleAffichage != null ? tailleAffichage.toString() : ""); // On met la dernière valeur qu'on a mise
            listeDesThèmes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE); // Pour être sûr qu'on en ait qu'un

            final String[] fichiersThemes = new File("res/css").list();
            for (String fichier : fichiersThemes) {
                final String nomThème = fichier.split("-")[0];
                if (!listeDesThèmes.getItems().contains(nomThème)) {
                    listeDesThèmes.getItems().add(nomThème);
                }
            }

            // Ici on fait ça pour choisir automatiquement le thème actuel dans la liste quand on ouvre la fenêtre
            final String[] thème_actuel = Application.getUserAgentStylesheet().split("/");
            listeDesThèmes.getSelectionModel().select(thème_actuel[thème_actuel.length - 1].split("-")[0]);

            listeDesThèmes.getSelectionModel().selectedItemProperty().addListener((observable, avant, apres) -> chargerTheme(apres));

            modeSombre.setSelected(thème_sombre);
            modeSombre.setOnAction(event -> {
                thème_sombre = !thème_sombre;
                chargerTheme(listeDesThèmes.getSelectionModel().getSelectedItem());

                // Là on rend chaque icône blanche ou noire en fonction de si on est en mode sombre ou pas
                Menu.effetIcones.setSaturation(thème_sombre ? -1 : 0);
                Menu.effetIcones.setBrightness(thème_sombre ? 1 : 0);
                Menu.effetIcones.setContrast(thème_sombre ? -1 : 0);
            });

            modeAbrégé.setSelected(mode_abrégé);
            modeAbrégé.setOnAction(event -> mode_abrégé = !mode_abrégé);

            champMaxLignes.textProperty().addListener((observable, avant, apres) -> {
                try {
                    tailleAffichage = Short.parseShort(apres);
                } catch (NumberFormatException e) { // Avec un Integer ça faisait une stack overflow error si on mettait des lettres (mais apparemment Short non?)
                    tailleAffichage = null;
                }
            });

            // Sert à empêcher les lettres etc dans le champ
            champMaxLignes.setOnKeyReleased(event -> {
                champMaxLignes.setText(tailleAffichage != null ? tailleAffichage.toString() : ""); // j'ai remarqué qu'on peut pas faire setText dans la lambda du dessus apparemment: https://bugs.openjdk.org/browse/JDK-8081700 (on avait la même erreur)
            });

            final Scene scene = new Scene(root);
            // Raccourci pour fermer la fenêtre avec échap (utile pour mon linux sans décorations de fenêtres, donc sans bouton de fermeture)
            scene.setOnKeyReleased(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    fenêtreParamètres.close();
                }
            });

            fenêtreParamètres.initOwner(stage);
            fenêtreParamètres.initModality(Modality.WINDOW_MODAL);
            fenêtreParamètres.setScene(scene);
            fenêtreParamètres.setResizable(false);
            fenêtreParamètres.show();
        } catch (IOException e) {}
    }
}
