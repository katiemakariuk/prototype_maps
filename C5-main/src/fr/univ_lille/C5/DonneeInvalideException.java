package fr.univ_lille.C5;

public class DonneeInvalideException extends Exception {
    public DonneeInvalideException(String ligne) {
        super("Ligne invalide : "+ligne);
    }
}
