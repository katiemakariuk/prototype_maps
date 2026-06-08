package fr.univ_lille.C5;

import fr.ulille.but.sae_s2_2026.Connexion;
import fr.ulille.but.sae_s2_2026.Lieu;
import fr.ulille.but.sae_s2_2026.ModaliteTransport;

import java.util.HashMap;
import java.util.Objects;

public class Route implements Connexion {
    private final ModaliteTransport modalite;
    private final Lieu depart;
    private final Lieu arrivee;
    private final HashMap<TypeCout, Double> couts;

    public static Route avecCSV(String ligneCSV) {
        String[] elements = ligneCSV.split(";");
        if (elements.length != 6) return null;

        String villeDepart = elements[0];
        String villeArrivee = elements[1];
        ModaliteTransport modalite = ModaliteTransport.valueOf(elements[2].toUpperCase());
        int cout = Integer.parseInt(elements[3]);
        double emissionCO2 = Double.parseDouble(elements[4]);
        int duree = Integer.parseInt(elements[5]);
        if (cout < 0 || emissionCO2 < 0 || duree < 0) return null;

        return new Route(new Ville(villeDepart), new Ville(villeArrivee), modalite, cout, emissionCO2, duree);
    }

    public double getCout(TypeCout type) {
        return couts.getOrDefault(type, 0.0);
    }

    // Ici on donne plus de poids aux critères en fonction de leur priorité (donc en gros leur position dans la liste des critères)
    public double getCout(Voyageur voyageur) {
        double score = 0.0;
        int n = voyageur.critères.size();
        for (int i = 0; i < n; i++) {
            score += getCout(voyageur.critères.get(i)) * (n - i);
        }
        return score;
    }

    @Override
    public Lieu getDepart() {
        return depart;
    }

    @Override
    public Lieu getArrivee() {
        return arrivee;
    }

    @Override
    public ModaliteTransport getModalite() {
        return modalite;
    }

    public Route createRouteAvecVilleArrivée() {
        return new Route(depart, new Ville(arrivee + "-arrivée"), modalite, couts);
    }

    public Route createRouteAvecModalite() {
        return new Route(new Ville(depart + "-" + modalite), new Ville(arrivee + "-" + modalite), modalite, couts);
    }

    public Route createRouteAvecModaliteEtVilleOrigine() {
        return new Route(depart, new Ville(arrivee + "-" + modalite), modalite, couts);
    }

    public Route createRouteAvecModaliteEtVilleArrivée() {
        return new Route(new Ville(depart + "-" + modalite), new Ville(arrivee + "-arrivée"), modalite, couts);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Route other = (Route) obj;
        return depart.equals(other.depart) && arrivee.equals(other.arrivee) && modalite == other.modalite;
    }

    @Override
    public int hashCode() { // Généré par l'IDE (en choisissant cependant les champs nous-même)
        int result = Objects.hashCode(getModalite());
        result = 31 * result + getDepart().hashCode();
        result = 31 * result + getArrivee().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return depart + " -> " + arrivee + " (" + modalite + ")";
    }

    private Route(Lieu depart, Lieu arrivee, ModaliteTransport modalite, HashMap<TypeCout, Double> couts) {
        this.depart = depart;
        this.arrivee = arrivee;
        this.modalite = modalite;
        this.couts = couts;
    }

    public Route(Lieu depart, Lieu arrivee, ModaliteTransport modalite, double prix, double emissionCO2, double duree) {
        this(depart, arrivee, modalite, new HashMap<>());
        couts.put(TypeCout.PRIX, prix);
        couts.put(TypeCout.CO2, emissionCO2);
        couts.put(TypeCout.TEMPS, duree);
    }
}
