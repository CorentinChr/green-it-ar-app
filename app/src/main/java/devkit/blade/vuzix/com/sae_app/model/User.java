package devkit.blade.vuzix.com.sae_app.model;

import java.util.List;

/**
 * Modèle représentant l'utilisateur récupéré depuis l'API.
 */
public class User {
    public String userId;
    public String name;
    // level attendu: "beginner" | "intermediate" | "advanced"
    public String level;
    public int score;
    public List<Integer> scoreHistory;

    public String getName() { return name; }

    public String getLevel() { return level; }

    /**
     * Retourne la valeur numérique du niveau: beginner=1, intermediate=2, advanced=3
     */
    public int getLevelValue() {
        if (level == null) return 1;
        switch (level.toLowerCase()) {
            case "beginner":
            case "débutant":
                return 1;
            case "intermediate":
            case "moyen":
                return 2;
            case "advanced":
            case "avancé":
            case "avance":
                return 3;
            default:
                // si l'API renvoie un entier sous forme de texte
                try {
                    int v = Integer.parseInt(level);
                    if (v >= 1 && v <= 3) return v;
                } catch (Exception ignored) {}
                return 1;
        }
    }
}

