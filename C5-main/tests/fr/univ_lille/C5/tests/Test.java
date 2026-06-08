package fr.univ_lille.C5.tests;

public abstract class Test {
    // Méthodes statiques pour éviter qu'on répète du code
    protected static void bon() {
        System.out.print(" ✓");
    }

    protected static void erreur(String erreur) {
        System.err.println("✗ Erreur : "+erreur);
    }

    protected static void erreur(String attendu, String obtenu) {
        erreur("attendu "+attendu+", obtenu "+obtenu);
    }

    protected static void tests_terminés() {
        System.out.println("\n==========================");
        System.out.println("     TESTS TERMINÉS ");
        System.out.println("==========================\n");
    }
}