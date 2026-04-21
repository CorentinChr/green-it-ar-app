package devkit.blade.vuzix.com.sae_app.model;

import java.util.List;

/**
 * Modèle représentant une question du quiz récupérée depuis le backend.
 */
public class QuizItem {
    public int id;
    public String question;
    public List<String> answers;
    public int correctIndex;

    // Nouvelle propriété : information affichée lorsqu'on répond correctement
    // Exemple : "Bonne réponse ! Le saviez-vous : ..."
    public String infos;

    // Thème de la question (doit correspondre au champ JSON renvoyé par l'API)
    public String theme;

    public int difficulty; // 1=facile, 2=moyen, 3=difficile

    // Getter utile pour accéder au thème de manière sûre
    public String getTheme() {
        return theme;
    }

    /**
     * Normalise la difficulté renvoyée par l'API.
     * Certains endpoints renvoient 0/1/2 (zéro-based) tandis que d'autres renvoient 1/2/3.
     * - Si la valeur est 0, on la mappe à 1.
     * - Si la valeur est déjà 1..3, on la retourne telle quelle.
     */
    public int getEffectiveDifficulty() {
        if (difficulty == 0) return 1;
        if (difficulty >= 1 && difficulty <= 3) return difficulty;
        // fallback : retourne la valeur brute
        return difficulty;
    }
}