package fr.univtln.capteurs;

/**
 * Fonction mathématique du temps : x = secondes écoulées, résultat = valeur théorique.
 */
@FunctionalInterface
public interface Function {
    double apply(long x);
}
