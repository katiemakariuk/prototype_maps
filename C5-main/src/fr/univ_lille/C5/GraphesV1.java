package fr.univ_lille.C5;

import fr.ulille.but.sae_s2_2026.*;

import java.util.List;

public class GraphesV1 {
    public static void main(String[] args) throws DonneeInvalideException {
        final String[] data = new String[]{
                // Train:
                "Bordeaux;Paris;Train;236;1.66;197",
                "Bordeaux;Strasbourg;Train;389;2.99;324",
                "Bordeaux;Lille;Train;323;2.33;269",
                "Bordeaux;Brest;Train;260;1.89;217",
                "Paris;Strasbourg;Train;196;1.34;164",
                "Paris;Lille;Train;90;0.67;76",
                "Paris;Brest;Train;236;1.83;197",
                "Strasbourg;Lille;Train;200;1.99;167",
                "Strasbourg;Brest;Train;428;3.17;357",
                "Lille;Brest;Train;304;2.53;254",

                // Bus:
                "Bordeaux;Paris;Bus;88;71.2;393",
                "Bordeaux;Strasbourg;Bus;146;118;648",
                "Bordeaux;Lille;Bus;121;97.3;538",
                "Bordeaux;Brest;Bus;98;78.3;433",
                "Paris;Strasbourg;Bus;74;59.4;327",
                "Paris;Lille;Bus;33;27.5;147",
                "Paris;Brest;Bus;90;71.9;400",
                "Strasbourg;Lille;Bus;75;69.3;333",
                "Strasbourg;Brest;Bus;158;130;700",
                "Lille;Brest;Bus;105;92.9;467",

                // Avion:
                "Bordeaux;Paris;Avion;413;112;44",
                "Bordeaux;Strasbourg;Avion;680;170;73",
                "Paris;Strasbourg;Avion;343;89.2;37",
        };

        MultiGrapheOrienteValue multigraphe = new MultiGrapheOrienteValue();
        for (String ligne: data) {
            final Route route = Route.avecCSV(ligne);
            if (route == null) {
                System.out.println("Ligne invalide : " + ligne);
                return;
            }

            final Lieu départ = route.getDepart();
            final Lieu arrivée = route.getArrivee();
            if (!multigraphe.sommets().contains(départ)) {
                multigraphe.ajouterSommet(départ);
            }
            if (!multigraphe.sommets().contains(arrivée)) {
                multigraphe.ajouterSommet(arrivée);
            }

            if (route.getModalite() == ModaliteTransport.TRAIN) { // Critère n°1 de notre V1
                multigraphe.ajouterArete(route, /* Critère n°2 de notre V1 */ route.getCout(TypeCout.PRIX));
            }
        }

        // Version 1: Exemple de prototype
        List<Chemin> algorithme = AlgorithmeKPCC.kpcc(multigraphe, new Ville("Bordeaux"), new Ville("Brest"), 4);
        for (Chemin chemin: algorithme) {
            for (int i = 0; i < chemin.aretes().size() - 1; i++) {
                System.out.print(chemin.aretes().get(i).getDepart() + " -> ");
            }
            System.out.println(chemin.aretes().getLast() + " (" + chemin.poids() + "€)");
        }
    }
}
