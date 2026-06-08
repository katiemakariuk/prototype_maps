package fr.univ_lille.C5;

import fr.ulille.but.sae_s2_2026.Lieu;

public class Ville implements Lieu {
    private final String nom;

    public Ville(String ville) {
        nom = ville;
    }

    @Override
    public String toString() {
        return nom;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Ville autre = (Ville) o;
        return nom.equals(autre.nom);
    }

    @Override
    public int hashCode() {
        return nom.hashCode();
    }
}
