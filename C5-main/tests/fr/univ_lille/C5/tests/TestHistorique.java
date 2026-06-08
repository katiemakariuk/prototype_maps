package fr.univ_lille.C5.tests;

import fr.univ_lille.C5.Historique;
import fr.univ_lille.C5.TypeCout;
import fr.univ_lille.C5.VoyageEffectue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.List;

public class TestHistorique extends Test {
    public static void main(String[] args) throws IOException {
        Path fichier = Files.createTempFile("c5-historique-", ".bin");
        Files.deleteIfExists(fichier);

        Historique historique = new Historique(fichier);
        historique.ajouter(new VoyageEffectue("Lille", "Paris",
            Map.of(TypeCout.PRIX, 50.0, TypeCout.CO2, 4.0, TypeCout.TEMPS, 80.0),
            List.of(TypeCout.CO2)));
        historique.ajouter(new VoyageEffectue("Paris", "Lyon",
            Map.of(TypeCout.PRIX, 70.0, TypeCout.CO2, 3.0, TypeCout.TEMPS, 120.0),
            List.of(TypeCout.CO2)));

        Historique recharge = new Historique(fichier);
        if (recharge.taille() == 2) bon();
        else erreur("2 voyages apres rechargement", "" + recharge.taille());

        if (recharge.voyagesRecents(1).getFirst().getTrajet().equals("Paris -> Lyon")) bon();
        else erreur("le voyage le plus recent est incorrect");

        if (recharge.evolution(TypeCout.CO2).getLast().contains("-1.0")) bon();
        else erreur("l'evolution du CO2 est incorrecte");

        if (recharge.moyenne(TypeCout.PRIX).startsWith("60.0")) bon();
        else erreur("la moyenne du prix est incorrecte");

        if (recharge.analysePersonnalisee().contains("CO2")) bon();
        else erreur("le centre d'interet principal est incorrect");

        recharge.vider();
        if (new Historique(fichier).taille() == 0) bon();
        else erreur("l'historique n'a pas ete vide");

        Files.deleteIfExists(fichier);
        tests_terminés();
    }
}
