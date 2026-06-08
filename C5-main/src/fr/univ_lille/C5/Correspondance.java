package fr.univ_lille.C5;

import fr.ulille.but.sae_s2_2026.Lieu;
import fr.ulille.but.sae_s2_2026.ModaliteTransport;

import java.util.HashMap;

public class Correspondance { // Sert à représenter une correspondance faite à partir de CSV
    private final Lieu ville;
    private final ModaliteTransport de;
    private final ModaliteTransport vers;
    private final HashMap<TypeCout, Double> couts;

    public HashMap<TypeCout, Double> getCouts() {
        return couts;
    }

    public ModaliteTransport getModaliteDepart() {
        return de;
    }

    public ModaliteTransport getModalite() {
        return vers;
    }
    public Lieu getVille() {
        return ville;
    }

    public double getCout(TypeCout type, Route routeOrigine) {
        return routeOrigine.getCout(type) + couts.getOrDefault(type, 0.0);
    }

    public String getKey() {
        return ville + "#" + de;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Correspondance that = (Correspondance) o;
        return getVille().equals(that.getVille()) && vers == that.vers && de == that.de;
    }

    @Override
    public int hashCode() { // Généré par l'IDE (en choisissant cependant les champs nous-même)
        int result = getVille().hashCode();
        result = 31 * result + vers.hashCode();
        return result;
    }

    public Correspondance(Lieu ville, ModaliteTransport de, ModaliteTransport vers, int prix, double emission, int duree) {
        this.ville = ville;
        this.de = de;
        this.vers = vers;
        this.couts = new HashMap<>();
        this.couts.put(TypeCout.PRIX, (double) prix);
        this.couts.put(TypeCout.CO2, emission);
        this.couts.put(TypeCout.TEMPS, (double) duree);
    }
}
