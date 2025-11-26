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

    // Getter utile pour accéder au thème de manière sûre
    public String getTheme() {
        return theme;
    }
}