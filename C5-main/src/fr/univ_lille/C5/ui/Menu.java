package fr.univ_lille.C5.ui;

import fr.ulille.but.sae_s2_2026.AlgorithmeKPCC;
import fr.ulille.but.sae_s2_2026.Chemin;
import fr.ulille.but.sae_s2_2026.ModaliteTransport;
import fr.ulille.but.sae_s2_2026.MultiGrapheOrienteValue;
import fr.univ_lille.C5.*;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Menu extends Application {
    private static MultiGrapheOrienteValue multigraphe = null;
    private static final Voyageur voyageur = new Voyageur(List.of(), null);
    private static final Historique historique = new Historique();
    private static final ArrayList<String> fichiers = new ArrayList<>(List.of("res/routes.csv", "res/correspondances.csv"));

    public static final ColorAdjust effetIcones = new ColorAdjust(); // J'ai trouvé ça pour changer la couleur quand on passe en thème sombre

    private static void resetLeGraphe() throws IOException, DonneeInvalideException {
        multigraphe = GraphesV2.multigrapheTrajets(voyageur, GraphesV2.getCSV(fichiers.get(0)), GraphesV2.getCSV(fichiers.get(1)));
    }

    private static Button creerBouton(List<Chemin> chemins, int i, String[] textes) {
        final Chemin chemin = chemins.get(i);
        Button effectuer = new Button(textes[i]);
        effectuer.setMaxWidth(Double.MAX_VALUE);
        effectuer.setOnAction(event -> {
            try {
                historique.ajouter(VoyageEffectue.depuisChemin(chemin, voyageur.critères));
                effectuer.setText("Voyage effectué (enregistré dans l'historique)");
                effectuer.setDisable(true);
            } catch (IOException e) {
                effectuer.setText("Impossible d'enregistrer le voyage");
            }
        });
        return effectuer;
    }

    public void start(Stage stage) throws IOException, DonneeInvalideException {
        // Des thèmes qu'on a trouvé ici: https://github.com/mkpaz/atlantafx
        Parametres.chargerTheme("nord");

        FXMLLoader loader = new FXMLLoader();
        URL fxmlFileUrl = getClass().getResource("menu.fxml");
        loader.setLocation(fxmlFileUrl);
        Parent root = loader.load();

        Scene scene = new Scene(root);

        final TextField champRecherche = (TextField) root.lookup("#barre_de_recherche_depart");
        final TextField champRechercheArrivee = (TextField) root.lookup("#barre_de_recherche_arrivee");
        final ListView<Button> suggestions = (ListView<Button>) root.lookup("#suggestions");
        // On a mis Labeled, comme ça pas de problème pour mettre un label au lieu d'un bouton quand on a 0 trajets trouvés
        final ListView<Labeled> meilleurs_trajets = (ListView<Labeled>) root.lookup("#meilleurs_trajets");
        final RadioButton boutonAucuneModalité = (RadioButton) root.lookup("#rb_aucune_modalite");
        final Group groupeCritèresCouts = (Group) root.lookup("#couts");
        final Button boutonParametres = (Button) root.lookup("#parametres");
        final ButtonBar tripletBoutons = (ButtonBar) root.lookup("#triplet_boutons");
        final MenuBar barreMenu = (MenuBar) root.lookup("#barre_menu");
        // Ici j'ai dû écrire le package entier parce que notre classe s'appelle déjà Menu
        final javafx.scene.control.Menu menuImportation = (javafx.scene.control.Menu) barreMenu.getMenus().getFirst().getItems().getFirst();
        final Button boutonHistorique = (Button) root.lookup("#historique");

        // Sous-fenêtres
        boutonParametres.setOnAction(event -> Parametres.start(stage));
        boutonHistorique.setOnAction(event -> {
            Stage fenetreHistorique = new Stage();
            fenetreHistorique.initOwner(stage);
            fenetreHistorique.initModality(Modality.WINDOW_MODAL);
            Enregistrements.afficher(fenetreHistorique, historique);
        });

        resetLeGraphe();

        champRecherche.focusedProperty().addListener((observable, avant, apres) -> suggestions.setVisible(apres));
        champRechercheArrivee.focusedProperty().addListener((observable, avant, apres) -> suggestions.setVisible(apres));

        final Consumer<Boolean> miseÀJourTrajets = (conditionEnPlus) -> { // On a choisi d'utiliser ça pour éviter de répéter du code
            try {
                if ((champRecherche.getText().isEmpty() || champRechercheArrivee.getText().isEmpty()) && conditionEnPlus) {
                    suggestions.setVisible(false);
                    throw new VoyageImpossibleException(); // Pour pas avoir à réécrire le try-catch
                }

                meilleurs_trajets.setDisable(false);
                meilleurs_trajets.setVisible(true);

                resetLeGraphe();

                List<Chemin> chemins = AlgorithmeKPCC.kpcc(
                    multigraphe, new Ville(champRecherche.getText()), new Ville(champRechercheArrivee.getText()+"-arrivée"),
                    Parametres.tailleAffichage != null ? Parametres.tailleAffichage : 9999
                );
                String[] textes = GraphesV2.trajets(chemins, voyageur.critères, Parametres.mode_abrégé, null).replace("\r", "").split("\n");

                meilleurs_trajets.getItems().clear();
                if (textes.length == 0) throw new VoyageImpossibleException(); // Pareil, pour pas avoir à réécrire le try-catch

                // Ici on gère les trajets/l'historique
                for (int i = 0; i < textes.length; i++) {
                    Button effectuer = creerBouton(chemins, i, textes);
                    meilleurs_trajets.getItems().add(effectuer);
                }
            } catch (VoyageImpossibleException | IllegalArgumentException e) {
                suggestions.getItems().clear();
                meilleurs_trajets.getItems().clear();
                meilleurs_trajets.setDisable(true);
                meilleurs_trajets.getItems().add(new Label("Aucun trajet trouvé"));
            } catch (DonneeInvalideException | IOException e) {
                System.err.println(e);
            }
        };

        final EventHandler<KeyEvent> eventHandlerPourLieux = event -> { // Pareil, pour pas réécrire
            miseÀJourTrajets.accept(event.getCode() == KeyCode.BACK_SPACE); // Règle un problème qu'on avait si on appuyait sur la touche windows par exemple
        };

        champRecherche.setOnKeyReleased(eventHandlerPourLieux);
        champRechercheArrivee.setOnKeyReleased(eventHandlerPourLieux);

        boutonAucuneModalité.getToggleGroup().selectedToggleProperty().addListener((observable, avant, apres) -> {
            if (apres == boutonAucuneModalité) {
                voyageur.modalite = null;
            } else {
                voyageur.modalite = ModaliteTransport.valueOf(((RadioButton) apres).getText().toUpperCase());
            }
            miseÀJourTrajets.accept(true);
        });

        // Ici on gère les critères (et leurs priorités quoi)
        final List<Node> boites = groupeCritèresCouts.getChildren();
        for (Node élément : boites) {
            ChoiceBox<TypeCout> boiteActuelle = (ChoiceBox<TypeCout>) élément;
            boiteActuelle.getItems().add(null);
            boiteActuelle.getItems().addAll(TypeCout.values());
            boiteActuelle.getSelectionModel().selectedItemProperty().addListener((observable, avant, apres) -> {
                if (avant != apres) {
                    int indexActuel = boites.indexOf(élément);
                    if (indexActuel + 1 != boites.size()) {
                        ChoiceBox<TypeCout> prochain = (ChoiceBox<TypeCout>) boites.get(indexActuel + 1);
                        if (apres == null) {
                            prochain.setDisable(true);
                            prochain.getSelectionModel().select(null); // techniquement ça va appeler ma lambda et se propager aux prochains donc ça m'arrange
                        } else {
                            prochain.setDisable(false);
                        }
                    }
                }

                voyageur.critères.clear();
                for (Node élément2 : boites) {
                    TypeCout selection = ((ChoiceBox<TypeCout>) élément2).getSelectionModel().getSelectedItem();
                    if (selection != null) {
                        voyageur.critères.add(selection);
                    }
                }

                miseÀJourTrajets.accept(true);
            });

            boiteActuelle.setOnShowing(event -> { // Ici je m'assure d'enlever les critères qu'on a déjà choisis
                ArrayList<TypeCout> valeursAAfficher = new ArrayList<>(List.of(TypeCout.values()));
                valeursAAfficher.removeAll(voyageur.critères);
                boiteActuelle.getItems().removeAll(TypeCout.values());
                boiteActuelle.getItems().addAll(valeursAAfficher);
            });
        }

        // Enfin on rajoute les images aux boutons
        final String[] images = {"house", "enterprise", "nature", "settings"};
        final List<Node> boutons = new ArrayList<>(tripletBoutons.getButtons());
        boutons.add(boutonParametres);
        for (int i = 0; i < images.length; i++) {
            ImageView vueImage = new ImageView(new Image("file:res/img/" + images[i] + "_48dp.png"));
            vueImage.setFitHeight(36);
            vueImage.setFitWidth(36);
            vueImage.setEffect(effetIcones);
            ((Button) boutons.get(i)).setGraphic(vueImage);
        }

        // Ici on fait en sorte que les menus permettent d'importer les fichiers
        for (MenuItem sousMenu: menuImportation.getItems()) {
            sousMenu.setOnAction(event -> {
                File fichierChoisi = new FileChooser().showOpenDialog(stage);
                if (fichierChoisi != null && fichierChoisi.canRead()) {
                    fichiers.set(menuImportation.getItems().indexOf(sousMenu), fichierChoisi.getAbsolutePath());
                }
            });
        }

        // Bouton quitter dans le menu fichiers
        barreMenu.getMenus().getFirst().getItems().getLast().setOnAction(event -> {
            stage.close();
        });

        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Menu");
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
