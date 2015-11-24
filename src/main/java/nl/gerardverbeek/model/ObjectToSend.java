package nl.gerardverbeek.model;

/**
 * Created by gerardverbeek on 06/11/15.
 */
public class ObjectToSend {

    boolean sent = false;
    boolean arrived = false;

    public boolean isArrived() {
        return arrived;
    }

    public void setArrived(boolean arrived) {
        this.arrived = arrived;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
