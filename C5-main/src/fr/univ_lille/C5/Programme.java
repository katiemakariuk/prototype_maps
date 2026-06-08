package fr.univ_lille.C5;

import fr.ulille.but.sae_s2_2026.AlgorithmeKPCC;
import fr.ulille.but.sae_s2_2026.Chemin;
import fr.ulille.but.sae_s2_2026.ModaliteTransport;
import fr.ulille.but.sae_s2_2026.MultiGrapheOrienteValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Programme {
    public static void main(String[] args) {
        final Historique historique = new Historique();

        try {
            if (args.length == 1 && args[0].equalsIgnoreCase("--historique")) {
                afficherHistorique(historique);
                return;
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("--vider-historique")) {
                historique.vider();
                System.out.println("Historique vide.");
                return;
            }

            final String[] data = GraphesV2.getCSV(args[0]);
            final String[] correspondances = GraphesV2.getCSV(args[1]);

            final List<TypeCout> critères = new ArrayList<>();
            if (!args[5].equals("aucuncritère")) {
                for (String crit : args[5].split(",")) {
                    critères.add(TypeCout.valueOf(crit.trim().toUpperCase()));
                }
            }

            final ModaliteTransport modalité_choisie = args[4].equalsIgnoreCase("toustransports") ? null : ModaliteTransport.valueOf(args[4].toUpperCase());
            final Double borne_max = args.length >= 9 ? Double.parseDouble(args[8]) : null;

            final Voyageur voyageur = new Voyageur(critères, modalité_choisie);
            MultiGrapheOrienteValue multigraphe = GraphesV2.multigrapheTrajets(voyageur, data, correspondances);
            List<Chemin> algorithme = AlgorithmeKPCC.kpcc(multigraphe, new Ville(args[2]), new Ville(args[3]+"-arrivée"), Integer.parseUnsignedInt(args[6]));

            System.out.print(GraphesV2.trajets(algorithme, critères, args[7].equals("abrégé"), borne_max));
        } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            System.err.println("Arguments incorrects");
            System.out.println("Usage: java -jar ./C5.jar /chemin/vers/routes.csv /chemin/vers/correspondances.csv villeDépart villeArrivée transport(toustransports/bus/train/avion) critères(aucuncritère/prix/co2/temps/prix,co2,temps) nombre affichage(complet/abrégé) borne_max_optionelle");
        } catch (VoyageImpossibleException e) {
            System.err.println("Voyage impossible entre "+args[2]+" et "+args[3]+" (du moins en tenant compte des critères fournis).");
        } catch (DonneeInvalideException | IOException e) {
            System.err.println(e);
        }
    }

    private static void afficherHistorique(Historique historique) {
        System.out.println("=== Evolution du prix ===");
        historique.evolution(TypeCout.PRIX).forEach(System.out::println);
        System.out.println("\n=== Evolution du CO2 ===");
        historique.evolution(TypeCout.CO2).forEach(System.out::println);
        System.out.println("\n=== Evolution du temps ===");
        historique.evolution(TypeCout.TEMPS).forEach(System.out::println);
    }
}
