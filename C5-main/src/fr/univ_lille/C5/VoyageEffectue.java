package fr.univ_lille.C5;

import fr.ulille.but.sae_s2_2026.Chemin;
import fr.ulille.but.sae_s2_2026.Connexion;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class VoyageEffectue implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final String depart;
    private final String arrivee;
    private final EnumMap<TypeCout, Double> couts;
    private final ArrayList<TypeCout> centresInteret;
    private final LocalDateTime date;

    public VoyageEffectue(String depart, String arrivee, Map<TypeCout, Double> couts,
                          List<TypeCout> centresInteret) {
        this(depart, arrivee, couts, centresInteret, LocalDateTime.now());
    }

    public VoyageEffectue(String depart, String arrivee, Map<TypeCout, Double> couts,
                          List<TypeCout> centresInteret, LocalDateTime date) {
        this.depart = depart;
        this.arrivee = arrivee;
        this.couts = new EnumMap<>(TypeCout.class);
        this.couts.putAll(couts);
        this.centresInteret = new ArrayList<>(centresInteret);
        this.date = date;
    }

    public static VoyageEffectue depuisChemin(Chemin chemin, List<TypeCout> centresInteret) {
        List<Connexion> aretes = chemin.aretes();
        EnumMap<TypeCout, Double> couts = new EnumMap<>(TypeCout.class);
        for (TypeCout type : TypeCout.values()) {
            couts.put(type, 0.0);
        }
        for (Connexion connexion : aretes) {
            Route route = (Route) connexion;
            for (TypeCout type : TypeCout.values()) {
                couts.merge(type, route.getCout(type), Double::sum);
            }
        }

        String depart = nettoyerVille(aretes.getFirst().getDepart().toString());
        String arrivee = nettoyerVille(aretes.getLast().getArrivee().toString());
        return new VoyageEffectue(depart, arrivee, couts, centresInteret);
    }

    private static String nettoyerVille(String ville) {
        return ville.replace("-arrivée", "").replaceAll("-(TRAIN|BUS|AVION)$", "");
    }

    public double getCout(TypeCout type) {
        return couts.getOrDefault(type, 0.0);
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getTrajet() {
        return depart + " -> " + arrivee;
    }

    public String coutsFormates() {
        return format(getCout(TypeCout.PRIX)) + "€ | "
            + format(getCout(TypeCout.CO2)) + " kg CO2e | "
            + format(getCout(TypeCout.TEMPS)) + " min";
    }

    public TypeCout getCentreInteretPrincipal() {
        return centresInteret.isEmpty() ? null : centresInteret.getFirst();
    }

    private static String format(double valeur) {
        return String.valueOf(Math.round(valeur * 100.0) / 100.0);
    }

    @Override
    public String toString() {
        return date.format(FORMAT_DATE) + " - " + getTrajet() + " - " + coutsFormates();
    }
}
