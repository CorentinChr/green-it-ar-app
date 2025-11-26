package devkit.blade.vuzix.com.sae_app.model;

public class QuizHabitudesItem {
    public int id;
    public String question;

    public String answerA;
    public String answerB;
    public String answerC;

    public int scoreA;
    public int scoreB;
    public int scoreC;

    // Informations affichées après la réponse (optionnel)
    public String infos;

    public QuizHabitudesItem() {
    }

    public QuizHabitudesItem(int id, String question,
                             String answerA, int scoreA,
                             String answerB, int scoreB,
                             String answerC, int scoreC,
                             String infos) {
        this.id = id;
        this.question = question;
        this.answerA = answerA;
        this.answerB = answerB;
        this.answerC = answerC;
        this.scoreA = scoreA;
        this.scoreB = scoreB;
        this.scoreC = scoreC;
        this.infos = infos;
    }

    public String getAnswer(int index) {
        switch (index) {
            case 0: return answerA;
            case 1: return answerB;
            case 2: return answerC;
            default: return null;
        }
    }

    public int getScoreForIndex(int index) {
        switch (index) {
            case 0: return scoreA;
            case 1: return scoreB;
            case 2: return scoreC;
            default: return 0;
        }
    }
}

