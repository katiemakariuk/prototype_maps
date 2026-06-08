package fr.univ_lille.C5;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Historique {
    private static final int TAILLE_MAX = 100;
    private static final Path FICHIER_PAR_DEFAUT =
        Path.of(System.getProperty("user.home"), ".c5", "historique-voyages.bin");

    private final Path fichier;
    private final ArrayList<VoyageEffectue> voyages;

    public Historique() {
        this(FICHIER_PAR_DEFAUT);
    }

    public Historique(Path fichier) {
        this.fichier = fichier;
        this.voyages = charger();
    }

    public void ajouter(VoyageEffectue voyage) throws IOException {
        voyages.add(voyage);
        while (voyages.size() > TAILLE_MAX) {
            voyages.removeFirst();
        }
        sauvegarder();
    }

    public List<VoyageEffectue> voyagesRecents(int nombre) {
        return voyages.reversed().stream().limit(nombre).toList();
    }

    public List<String> evolution(TypeCout type) {
        ArrayList<String> evolution = new ArrayList<>();
        VoyageEffectue precedent = null;
        for (VoyageEffectue voyage : voyages) {
            double valeur = voyage.getCout(type);
            String variation = "";
            if (precedent != null) {
                double difference = valeur - precedent.getCout(type);
                variation = " (" + (difference >= 0 ? "+" : "") + format(difference) + type + ")";
            }
            evolution.add(voyage.getTrajet() + " : " + format(valeur) + type + variation);
            precedent = voyage;
        }
        return evolution;
    }

    public String moyenne(TypeCout type) {
        if (voyages.isEmpty()) {
            return "Aucun voyage";
        }
        double moyenne = voyages.stream().mapToDouble(voyage -> voyage.getCout(type)).average().orElse(0);
        return format(moyenne) + type + " en moyenne";
    }

    public String analysePersonnalisee() {
        if (voyages.isEmpty()) {
            return "Effectuez un voyage pour obtenir une analyse personnalisée.";
        }

        Map<TypeCout, Integer> frequences = new EnumMap<>(TypeCout.class);
        for (VoyageEffectue voyage : voyages) {
            TypeCout centreInteret = voyage.getCentreInteretPrincipal();
            if (centreInteret != null) {
                frequences.merge(centreInteret, 1, Integer::sum);
            }
        }
        if (frequences.isEmpty()) {
            return "Aucun critère prioritaire n'a encore été exprimé.";
        }

        TypeCout favori = frequences.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .orElseThrow()
            .getKey();
        return "Votre critère prioritaire est " + favori.name() + " : " + moyenne(favori) + ".";
    }

    public int taille() {
        return voyages.size();
    }

    public void vider() throws IOException {
        voyages.clear();
        sauvegarder();
    }

    @SuppressWarnings("unchecked")
    private ArrayList<VoyageEffectue> charger() {
        if (!Files.exists(fichier)) {
            return new ArrayList<>();
        }
        try (ObjectInputStream entree = new ObjectInputStream(Files.newInputStream(fichier))) {
            return (ArrayList<VoyageEffectue>) entree.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            System.err.println("Historique illisible, un nouvel historique sera cree : " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void sauvegarder() throws IOException {
        Path dossier = fichier.getParent();
        if (dossier != null) {
            Files.createDirectories(dossier);
        }
        try (ObjectOutputStream sortie = new ObjectOutputStream(Files.newOutputStream(fichier))) {
            sortie.writeObject(voyages);
        }
    }

    private static String format(double valeur) {
        return String.valueOf(Math.round(valeur * 100.0) / 100.0);
    }
}
