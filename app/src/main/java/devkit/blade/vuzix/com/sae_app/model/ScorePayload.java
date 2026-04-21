package devkit.blade.vuzix.com.sae_app.model;

/**
 * Petit POJO utilisé pour envoyer un évènement de score au backend.
 * Exemples JSON envoyés : { "list": "qh_score_history", "score": 12, "timestamp": 1650000000000 }
 */
public class ScorePayload {
    public String list;
    // Keep the field name `score` for backend compatibility (integer on 10)
    public int score;
    public long timestamp;
    // Constructor sending only the integer score on 10
    public ScorePayload(String list, int score, long timestamp) {
        this.list = list;
        this.score = score;
        this.timestamp = timestamp;
    }
}

