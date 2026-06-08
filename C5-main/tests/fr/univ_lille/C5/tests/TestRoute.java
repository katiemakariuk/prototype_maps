package fr.univ_lille.C5.tests;

import fr.ulille.but.sae_s2_2026.ModaliteTransport;
import fr.univ_lille.C5.Route;
import fr.univ_lille.C5.TypeCout;
import fr.univ_lille.C5.Ville;

public class TestRoute extends Test {
    private static void testCreationAvecCSV() {
        System.out.println("=== Test Création avec CSV ===");

        // Test avec ligne valide
        String ligneValide = "Paris;Lille;Train;90;0.67;76";
        Route route1 = Route.avecCSV(ligneValide);
        if (route1 != null) {
            bon();
        } else {
            erreur("ligne valide pas transformée en route");
        }

        // Test avec ligne invalide (mauvais nombre de champs)
        String ligneInvalide1 = "Paris;Lille;Train;90";
        Route route2 = Route.avecCSV(ligneInvalide1);
        if (route2 == null) {
            bon();
        } else {
            erreur("une ligne invalide a été acceptée");
        }

        // Test avec valeurs négatives
        String ligneInvalide2 = "Paris;Lille;Train;-90;0.67;76";
        Route route3 = Route.avecCSV(ligneInvalide2);
        if (route3 == null) {
            bon();
        } else {
            erreur("ligne avec valeur négative acceptée");
        }

        // Test avec différentes modalités
        String ligneBus = "Bordeaux;Paris;Bus;88;71.2;393";
        Route routeBus = Route.avecCSV(ligneBus);
        if (routeBus != null && routeBus.getModalite() == ModaliteTransport.BUS) {
            bon();
        }

        String ligneAvion = "Bordeaux;Paris;Avion;413;112;44";
        Route routeAvion = Route.avecCSV(ligneAvion);
        if (routeAvion != null && routeAvion.getModalite() == ModaliteTransport.AVION) {
            bon();
        }

        System.out.println();
    }

    private static void testEquals() {
        System.out.println("=== Test Equals et HashCode ===");

        Ville paris1 = new Ville("Paris");
        Ville lille1 = new Ville("Lille");
        Route route1 = new Route(paris1, lille1, ModaliteTransport.TRAIN, 90, 0.67, 76);

        Ville paris2 = new Ville("Paris");
        Ville lille2 = new Ville("Lille");
        Route route2 = new Route(paris2, lille2, ModaliteTransport.TRAIN, 90, 0.67, 76);

        // Même route, devrait être égale
        if (route1.equals(route2)) {
            bon();
        } else {
            erreur("des routes identiques ne sont pas égales");
        }

        // Même hashcode
        if (route1.hashCode() == route2.hashCode()) {
            bon();
        } else {
            erreur("hashCodes différents pour routes égales");
        }

        // Route différente (prix différent mais même trajet)
        Route route3 = new Route(paris1, lille1, ModaliteTransport.TRAIN, 100, 0.67, 76);
        if (route1.equals(route3)) {
            bon();
        } else {
            erreur("routes avec même trajet ne sont pas égales");
        }

        // Route avec modalité différente
        Route route4 = new Route(paris1, lille1, ModaliteTransport.BUS, 90, 0.67, 76);
        if (!route1.equals(route4)) {
            bon();
        } else {
            erreur("routes avec modalités différentes sont égales");
        }

        // Route avec arrivée différente
        Ville bordeaux = new Ville("Bordeaux");
        Route route5 = new Route(paris1, bordeaux, ModaliteTransport.TRAIN, 90, 0.67, 76);
        if (!route1.equals(route5)) {
            bon();
        } else {
            erreur("des routes avec arrivées différentes sont égales");
        }

        System.out.println();
    }

    private static void testGetCout() {
        System.out.println("=== Test GetCout ===");

        Route route = new Route(
            new Ville("Paris"),
            new Ville("Lille"),
            ModaliteTransport.TRAIN,
            90.5,
            0.67,
            76.0
        );

        double prix = route.getCout(TypeCout.PRIX);
        double co2 = route.getCout(TypeCout.CO2);
        double temps = route.getCout(TypeCout.TEMPS);

        if (prix == 90.5) {
            bon();
        } else {
            erreur("90.5", ""+prix);
        }

        if (co2 == 0.67) {
            bon();
        } else {
            erreur("attendu 0.67, obtenu " + co2);
        }

        if (temps == 76.0) {
            bon();
        } else {
            erreur("76.0", ""+temps);
        }

        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("      TEST DE LA CLASSE ROUTE");
        System.out.println("====================================\n");

        testCreationAvecCSV();
        testEquals();
        testGetCout();

        tests_terminés();
    }
}
