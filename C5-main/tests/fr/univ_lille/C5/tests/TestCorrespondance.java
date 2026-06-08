package fr.univ_lille.C5.tests;

import fr.ulille.but.sae_s2_2026.ModaliteTransport;
import fr.univ_lille.C5.Correspondance;
import fr.univ_lille.C5.TypeCout;
import fr.univ_lille.C5.Ville;

public class TestCorrespondance extends Test {

    private static void testCreation() {
        System.out.println("=== Test Création Correspondance ===");

        Ville paris = new Ville("Paris");
        Correspondance corr = new Correspondance(
            paris,
            ModaliteTransport.TRAIN,
            ModaliteTransport.BUS,
            10,
            0.5,
            15
        );

        System.out.println("Correspondance créée");
        System.out.println("Ville : " + corr.getVille());
        System.out.println("De : " + corr.getModaliteDepart());
        System.out.println("Vers : " + corr.getModalite());
    }

    private static void testEquals() {
        System.out.println("=== Test Equals ===");

        Ville paris1 = new Ville("Paris");
        Ville paris2 = new Ville("Paris");
        Ville lille = new Ville("Lille");

        Correspondance corr1 = new Correspondance(
            paris1, ModaliteTransport.TRAIN, ModaliteTransport.BUS, 10, 0.5, 15
        );

        Correspondance corr2 = new Correspondance(
            paris2, ModaliteTransport.TRAIN, ModaliteTransport.BUS, 10, 0.5, 15
        );

        // Même correspondance
        if (corr1.equals(corr2)) {
            bon();
            System.out.println(" Les correspondances identiques sont égales");
        } else {
            erreur("les correspondances identiques ne sont pas égales");
        }

        // Correspondance avec prix différent (mais même trajet)
        Correspondance corr3 = new Correspondance(
            paris1, ModaliteTransport.TRAIN, ModaliteTransport.BUS, 20, 0.5, 15
        );

        if (corr1.equals(corr3)) {
            bon();
            System.out.println(" Les correspondances avec même trajet sont égales (prix ne compte pas)");
        } else {
            erreur("correspondances avec même trajet ne sont pas égales");
        }

        // Ville différente
        Correspondance corr4 = new Correspondance(
            lille, ModaliteTransport.TRAIN, ModaliteTransport.BUS, 10, 0.5, 15
        );

        if (!corr1.equals(corr4)) {
            bon();
            System.out.println(" Les correspondances avec villes différentes sont différentes");
        } else {
            erreur("les correspondances avec villes différentes sont égales");
        }

        // Modalité départ différente
        Correspondance corr5 = new Correspondance(
            paris1, ModaliteTransport.BUS, ModaliteTransport.BUS, 10, 0.5, 15
        );

        if (!corr1.equals(corr5)) {
            bon();
            System.out.println(" Les correspondances avec modalités de départ différentes sont différentes");
        } else {
            erreur("les correspondances avec modalités départ différentes sont égales");
        }

        // Modalité arrivée différente
        Correspondance corr6 = new Correspondance(
            paris1, ModaliteTransport.TRAIN, ModaliteTransport.AVION, 10, 0.5, 15
        );

        if (!corr1.equals(corr6)) {
            bon();
            System.out.println(" Les correspondances avec modalités arrivée différentes sont différentes");
        } else {
            erreur("les correspondances avec modalités arrivée différentes sont égales");
        }

        System.out.println();
    }

    private static void testHashCode() {
        System.out.println("=== Test HashCode ===");

        Ville paris1 = new Ville("Paris");
        Ville paris2 = new Ville("Paris");

        Correspondance corr1 = new Correspondance(
            paris1, ModaliteTransport.TRAIN, ModaliteTransport.BUS, 10, 0.5, 15
        );

        Correspondance corr2 = new Correspondance(
            paris2, ModaliteTransport.TRAIN, ModaliteTransport.BUS, 10, 0.5, 15
        );

        if (corr1.hashCode() == corr2.hashCode()) {
            bon();
            System.out.println(" HashCodes identiques pour correspondances égales");
        } else {
            erreur("hashCodes différents pour correspondances égales");
        }

        System.out.println();
    }

    private static void testCasUsage() {
        System.out.println("=== Test Cas d'Usage ===");

        // Simulation d'une correspondance Train->Bus à Paris
        Ville paris = new Ville("Paris");
        Correspondance corrParisTrainBus = new Correspondance(
            paris,
            ModaliteTransport.TRAIN,
            ModaliteTransport.BUS,
            5,  // 5€ de supplément
            0.1,    // 0.1 kg CO2
            10      // 10 minutes de temps de correspondance
        );

        System.out.println("Correspondance : TRAIN -> BUS à " + corrParisTrainBus.getVille());
        System.out.println("  Coût supplémentaire : " + corrParisTrainBus.getCouts().get(TypeCout.PRIX) + TypeCout.PRIX);
        System.out.println("  Temps de correspondance : " + corrParisTrainBus.getCouts().get(TypeCout.TEMPS) + TypeCout.TEMPS);
        System.out.println("  Impact CO2 : " + corrParisTrainBus.getCouts().get(TypeCout.CO2) + TypeCout.CO2);

        // Correspondance Bus->Train à Lille
        Ville lille = new Ville("Lille");
        Correspondance corrLilleBusTrain = new Correspondance(
            lille,
            ModaliteTransport.BUS,
            ModaliteTransport.TRAIN,
            8,  // 8€
            0.2,    // 0.2 kg CO2
            15      // 15 minutes
        );

        System.out.println("\nCorrespondance : BUS -> TRAIN à " + corrLilleBusTrain.getVille());
        System.out.println("  Coût supplémentaire : " + corrLilleBusTrain.getCouts().get(TypeCout.PRIX) + TypeCout.PRIX);
        System.out.println("  Temps de correspondance : " + corrLilleBusTrain.getCouts().get(TypeCout.TEMPS) + TypeCout.TEMPS);
        System.out.println("  Impact CO2 : " + corrLilleBusTrain.getCouts().get(TypeCout.CO2) + TypeCout.CO2);
    }

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   TEST DE LA CLASSE CORRESPONDANCE");
        System.out.println("========================================\n");

        testCreation();
        testEquals();
        testHashCode();
        testCasUsage();

        tests_terminés();
    }
}