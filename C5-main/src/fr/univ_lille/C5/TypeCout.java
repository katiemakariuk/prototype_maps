package fr.univ_lille.C5;

public enum TypeCout {
    CO2(" kg CO2e"), TEMPS(" minutes"), PRIX("€");

    private final String unité;

    @Override
    public String toString() {
        return unité;
    }

    TypeCout(String unité) {
        this.unité = unité;
    }
}
