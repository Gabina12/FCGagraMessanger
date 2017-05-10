package ge.bondx.fcgagramessanger.models;

/**
 * Created by Admin on 4/29/2017.
 */

public class ResultMessage {
    public boolean isFinished() {
        return IsFinished;
    }

    public void setFinished(boolean finished) {
        IsFinished = finished;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    private boolean IsFinished;
    private String Message;
}
