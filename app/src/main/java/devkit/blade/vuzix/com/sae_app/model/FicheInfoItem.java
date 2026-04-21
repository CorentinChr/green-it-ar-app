package devkit.blade.vuzix.com.sae_app.model;

/**
 * Modèle représentant une fiche d'information récupérée depuis le backend.
 */
public class FicheInfoItem {
    // Titre de la fiche
    public String titre;

    // Texte complet affiché dans la fiche
    public String texte;

    public int difficulty; // 1=facile, 2=moyen, 3=difficile

    // Getters utilitaires
    public String getTitre() {
        return titre;
    }

    public String getTexte() {
        return texte;
    }
}

