package fr.univ_lille.C5;

import fr.ulille.but.sae_s2_2026.ModaliteTransport;
import fr.ulille.but.sae_s2_2026.MultiGrapheOrienteValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Plateforme {
    private final HashSet<Route> routes;
    private final Voyageur voyageur;
    // Map qui permet de connaitre les correspondances possibles à partir d'une ville et une modalité de transport d'arrivée
    private final HashMap<String, List<Correspondance>> correspondancesMap;

    private void importCorrespondanceCSV(String[] csv) throws DonneeInvalideException {
        final HashSet<Correspondance> correspondances = new HashSet<>();

        // S'il n'y aucune ligne ou une seule ligne vide, on ne fait rien
        if (csv.length == 0 || (csv.length == 1 && csv[0].isEmpty())) return;

        for (String ligne : csv) {
            String[] elements = ligne.split(";");
            if (elements.length != 6) throw new DonneeInvalideException(ligne);

            try {
                String nomVille = elements[0];
                ModaliteTransport modalite1 = ModaliteTransport.valueOf(elements[1].toUpperCase());
                ModaliteTransport modalite2 = ModaliteTransport.valueOf(elements[2].toUpperCase());
                int cout = Integer.parseInt(elements[3]);
                double emissionCO2 = Double.parseDouble(elements[4]);
                int duree = Integer.parseInt(elements[5]);

                if (cout < 0 || emissionCO2 < 0 || duree < 0) throw new DonneeInvalideException(ligne);

                // Une correspondance se fait logiquement dans les deux sens
                correspondances.add(new Correspondance(new Ville(nomVille), modalite1, modalite2, cout, emissionCO2, duree));
                correspondances.add(new Correspondance(new Ville(nomVille), modalite2, modalite1, cout, emissionCO2, duree));
            } catch (IllegalArgumentException e) {
                throw new DonneeInvalideException(ligne);
            }
        }

        for (Correspondance correspondance : correspondances) {
            // ajout des correspondances dans la map
            List<Correspondance> correspondanceList = correspondancesMap.computeIfAbsent(correspondance.getKey(), k -> new ArrayList<>());
            correspondanceList.add(correspondance);
        }
    }

    private void importCSV(String[] csv) throws DonneeInvalideException {
        // création des routes correspondantes aux itinéraires entre villes

        for (String ligne : csv) {
            final Route route = Route.avecCSV(ligne);
            if (route == null) {
                throw new DonneeInvalideException(ligne);
            }

            routes.add(route);
        }
    }

    public MultiGrapheOrienteValue getMultigraphe() {
        final MultiGrapheOrienteValue multigraphe = new MultiGrapheOrienteValue();

        for (Route route : routes) {
            if (voyageur.modalite == null || route.getModalite() == voyageur.modalite) {
                Route routeAvecModalite = route.createRouteAvecModalite();
                Route routeAvecVilleArrivée = route.createRouteAvecVilleArrivée();
                Route routeAvecModaliteEtVilleOrigine = route.createRouteAvecModaliteEtVilleOrigine();
                Route routeAvecModaliteEtVilleArrivée = route.createRouteAvecModaliteEtVilleArrivée();

                multigraphe.ajouterSommet(route.getDepart());
                multigraphe.ajouterSommet(routeAvecVilleArrivée.getArrivee());
                multigraphe.ajouterSommet(routeAvecModalite.getDepart());
                multigraphe.ajouterSommet(routeAvecModalite.getArrivee());

                multigraphe.ajouterArete(routeAvecVilleArrivée, routeAvecVilleArrivée.getCout(voyageur));
                multigraphe.ajouterArete(routeAvecModalite, routeAvecModalite.getCout(voyageur));
                multigraphe.ajouterArete(routeAvecModaliteEtVilleOrigine, routeAvecModaliteEtVilleOrigine.getCout(voyageur));
                multigraphe.ajouterArete(routeAvecModaliteEtVilleArrivée, routeAvecModaliteEtVilleArrivée.getCout(voyageur));

                // création des routes pour modalité + correspondance
                List<Correspondance> correspondancesPossibles = correspondancesMap.get(route.getArrivee() + "#" + route.getModalite());
                if (correspondancesPossibles != null) {
                    for (Correspondance correspondance : correspondancesPossibles) {
                        final Ville sommetArrivéeAvecModalitéCorrespondanceCible = new Ville(route.getArrivee() + "-" + correspondance.getModalite());
                        multigraphe.ajouterSommet(sommetArrivéeAvecModalitéCorrespondanceCible);

                        final Route routeAvecCorrespondance = new Route(
                            routeAvecModalite.getDepart(), sommetArrivéeAvecModalitéCorrespondanceCible, route.getModalite(),
                            correspondance.getCout(TypeCout.PRIX, route), correspondance.getCout(TypeCout.CO2, route), correspondance.getCout(TypeCout.TEMPS, route)
                        );
                        multigraphe.ajouterArete(routeAvecCorrespondance, routeAvecCorrespondance.getCout(voyageur));

                        final Route routeAvecCorrespondanceEtVilleOrigine = new Route(
                            route.getDepart(), sommetArrivéeAvecModalitéCorrespondanceCible, route.getModalite(),
                            correspondance.getCout(TypeCout.PRIX, route), correspondance.getCout(TypeCout.CO2, route), correspondance.getCout(TypeCout.TEMPS, route)
                        );
                        multigraphe.ajouterArete(routeAvecCorrespondanceEtVilleOrigine, routeAvecCorrespondanceEtVilleOrigine.getCout(voyageur));
                    }
                }
            }
        }

        return multigraphe;
    }

    public Plateforme(Voyageur voyageur, String[] csv, String[] csvCorrespondances) throws DonneeInvalideException {
        routes = new HashSet<>();
        correspondancesMap = new HashMap<>();
        this.voyageur = voyageur;
        importCSV(csv);
        importCorrespondanceCSV(csvCorrespondances);
    }
}