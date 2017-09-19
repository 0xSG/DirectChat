package ap1.testbox.sooryagangarajk.com.perplechat;
/**
 * Created by Zeta Apponomics 3 on 24-11-2014.
 */
public class ChatMessage {
    public boolean left;
    public String message;

    public ChatMessage(boolean left, String message) {
        super();
        this.left = left;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}