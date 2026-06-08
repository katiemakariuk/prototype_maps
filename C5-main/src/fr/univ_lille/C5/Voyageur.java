package fr.univ_lille.C5;

import fr.ulille.but.sae_s2_2026.ModaliteTransport;

import java.util.ArrayList;
import java.util.List;

public class Voyageur {
    public ArrayList<TypeCout> critères; // ordonnée par priorité décroissante
    public ModaliteTransport modalite;

    public Voyageur(List<TypeCout> critères, ModaliteTransport modalite) {
        this.critères = new ArrayList<>(critères);
        this.modalite = modalite;
    }
}
