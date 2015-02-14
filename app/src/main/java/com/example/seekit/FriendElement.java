package com.example.seekit;

/**
 * Created by nicoB on 12/16/14.
 */
public class FriendElement {

    private String friendName;
    private String friendEmail;
    private boolean friendShare;


    public FriendElement(String friendName, String friendEmail, boolean friendShare){
        this.friendName=friendName;
        this.friendEmail=friendEmail;
        this.friendShare=friendShare;

    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getFriendEmail() {
        return friendEmail;
    }

    public void setFriendEmail(String friendEmail) {
        this.friendEmail = friendEmail;
    }

    public boolean isFriendShare() {
        return friendShare;
    }

    public void setFriendShare(boolean friendShare) {
        this.friendShare = friendShare;
    }
}
