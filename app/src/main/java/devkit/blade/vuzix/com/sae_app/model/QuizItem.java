package devkit.blade.vuzix.com.sae_app.model;

import java.util.List;

public class QuizItem {
    public int id;
    public String question;
    public List<String> answers;
    public int correctIndex;

    // Nouvelle propriété : information affichée lorsqu'on répond correctement
    // Exemple : "Bonne réponse ! Le saviez-vous : ..."
    public String infos;
}