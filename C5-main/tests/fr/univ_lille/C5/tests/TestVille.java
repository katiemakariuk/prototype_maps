package fr.univ_lille.C5.tests;

import fr.univ_lille.C5.Ville;

import java.util.HashSet;

public class TestVille extends Test {

    private static void testToString() {
        System.out.println("=== Test ToString ===");

        Ville lille = new Ville("Lille");
        String nom = lille.toString();

        if (nom.equals("Lille")) {
            bon();
        } else {
            erreur("attendu 'Lille', obtenu '"+ nom + "'");
        }

        System.out.println();
    }

    private static void testEquals() {
        System.out.println("=== Test Equals ===");

        Ville paris1 = new Ville("Paris");
        Ville paris2 = new Ville("Paris");
        Ville lille = new Ville("Lille");

        // Deux villes avec le même nom
        if (paris1.equals(paris2)) {
            bon();
        } else {
            erreur("des villes avec même nom ne sont pas égales");
        }

        // Deux villes avec noms différents
        if (!paris1.equals(lille)) {
            bon();
        } else {
            erreur("des villes avec noms différents sont égales");
        }

        // Comparaison avec null
        if (!paris1.equals(null)) {
            bon();
        } else {
            erreur("ville égale à null");
        }

        // Comparaison avec un autre type d'objet
        String str = "Paris";
        if (!paris1.equals(str)) {
            bon();
        } else {
            erreur("ville égale à un String");
        }

        System.out.println();
    }

    private static void testHashCode() {
        System.out.println("=== Test HashCode ===");

        Ville paris1 = new Ville("Paris");
        Ville paris2 = new Ville("Paris");
        Ville lille = new Ville("Lille");

        // Même hashcode pour villes égales
        if (paris1.hashCode() == paris2.hashCode()) {
            bon();
        } else {
            erreur("hashCodes différents pour des villes égales");
        }

        // Hashcodes probablement différents pour villes différentes
        if (paris1.hashCode() != lille.hashCode()) {
            bon();
        } else {
            erreur("hashCodes identiques pour villes différentes (collision possible)");
        }

        System.out.println();
    }

    private static void testDansHashSet() {
        System.out.println("=== Test dans HashSet ===");

        HashSet<Ville> villes = new HashSet<>();

        Ville paris1 = new Ville("Paris");
        Ville paris2 = new Ville("Paris");
        Ville lille = new Ville("Lille");

        villes.add(paris1);
        villes.add(paris2); // Ne devrait pas être ajouté (doublon)
        villes.add(lille);

        if (villes.size() == 2) {
            bon();
        } else {
            erreur("HashSet contient " + villes.size() + " villes (attendu 2)");
        }

        // Test de contains
        if (villes.contains(new Ville("Paris"))) {
            bon();
        } else {
            erreur("contains ne trouve pas Paris");
        }

        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("==========================");
        System.out.println(" TEST DE LA CLASSE VILLE");
        System.out.println("==========================\n");

        testToString();
        testEquals();
        testHashCode();
        testDansHashSet();

        tests_terminés();
    }
}