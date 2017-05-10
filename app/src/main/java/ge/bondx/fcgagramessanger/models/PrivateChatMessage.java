package ge.bondx.fcgagramessanger.models;

import java.util.Date;

/**
 * Created by Admin on 4/29/2017.
 */

public class PrivateChatMessage {
    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getMsgForm() {
        return MsgForm;
    }

    public void setMsgForm(String msgForm) {
        MsgForm = msgForm;
    }

    public String getMsgTo() {
        return MsgTo;
    }

    public void setMsgTo(String msgTo) {
        MsgTo = msgTo;
    }

    public String getMessageText() {
        return MessageText;
    }

    public void setMessageText(String messageText) {
        MessageText = messageText;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }

    public String isSeen() {
        return IsSeen;
    }

    public void setSeen(String seen) {
        IsSeen = seen;
    }

    private Integer Id;
    private String MsgForm;
    private String MsgTo;
    private String MessageText;
    private String CreateDate;
    private String IsSeen;

    public String getFromImage() {
        return FromImage;
    }

    public void setFromImage(String fromImage) {
        FromImage = fromImage;
    }

    public String getToImage() {
        return ToImage;
    }

    public void setToImage(String toImage) {
        ToImage = toImage;
    }

    private String FromImage;
    private String ToImage;
}
