package fr.univ_lille.C5;

import fr.ulille.but.sae_s2_2026.Chemin;
import fr.ulille.but.sae_s2_2026.Connexion;
import fr.ulille.but.sae_s2_2026.MultiGrapheOrienteValue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public abstract class GraphesV2 {
    public static MultiGrapheOrienteValue multigrapheTrajets(Voyageur voyageur, String[] données, String[] correspondances) throws DonneeInvalideException {
        return new Plateforme(voyageur, données, correspondances).getMultigraphe();
    }

    /**
     * @param chemins   Liste de chemins obtenus avec AlgorithmeKPCC.kpcc()
     * @param critères  Liste ordonnée des critères à afficher (tous les coûts sont recalculés depuis les arêtes — chemin.poids() n'est plus utilisé pour l'affichage)
     * @param abrégé    Si l'affichage doit être abrégé (GraphesV2) ou classique (comme GraphesV1)
     * @param borne_max Borne max sur le premier critère (null = pas de borne)
     **/
    public static String trajets(List<Chemin> chemins, List<TypeCout> critères, boolean abrégé, Double borne_max) throws VoyageImpossibleException {
        StringBuilder retour = new StringBuilder();

        if (chemins.isEmpty()) throw new VoyageImpossibleException();

        for (Chemin chemin : chemins) {
            List<Connexion> aretes = chemin.aretes();

            // Recalcul de chaque coût (RÉEL) en faisant la somme des arêtes (sans compter chemin.poids())
            double[] coutsTotaux = new double[critères.size()];
            for (Connexion arete : aretes) {
                for (int i = 0; i < critères.size(); i++) {
                    coutsTotaux[i] += ((Route) arete).getCout(critères.get(i));
                }
            }

            // La borne_max s'applique sur le premier critère (priorité la plus haute)
            if (borne_max == null || borne_max >= coutsTotaux[0]) {
                if (abrégé) retour.append(aretes.getFirst().getDepart()).append(" (").append(aretes.getFirst().getModalite()).append(")");

                for (int i = abrégé ? 1 : 0; i < aretes.size() - (abrégé ? 0 : 1); i++) {
                    if (!abrégé) {
                        retour.append(aretes.get(i).getDepart()).append(" -> ");
                    } else if (aretes.get(i).getModalite() != aretes.get(i - 1).getModalite()) {
                        retour.append(" -> ").append(aretes.get(i).getDepart());
                    }
                }

                retour.append(abrégé ? " -> " + aretes.getLast().getArrivee() : aretes.getLast());

                // Affichage de tous les coûts recalculés
                for (int i = 0; i < critères.size(); i++) {
                    retour.append(" (").append(Math.round(coutsTotaux[i]*100.0)/100.0).append(critères.get(i)).append(")");
                }
                retour.append("\n");
            }
        }

        return retour.toString().replace("-arrivée", "");
    }

    public static String[] getCSV(String cheminFichier) throws IOException {
        final File fichier = new File(cheminFichier);
        final FileReader lecteur = new FileReader(fichier);
        final char[] résultat = new char[(int) fichier.length()];

        while (lecteur.ready()) {
            lecteur.read(résultat);
        }

        return String.valueOf(résultat).replace("\r", "").split("\n");
    }

    public static void main(String[] args) {
        Programme.main(new String[]{
            "res/routes.csv", // <-- Fichier des routes
            "res/correspondances.csv", // <-- Fichier des correspondances
            "Bordeaux", "Brest", "toustransports", "temps", "4", "abrégé"
        });
    }
}
