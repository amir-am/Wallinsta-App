package androidapi.model.instagramapi;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Amir Hossein on 7/31/2017.
 */
@Entity
@Table(name = "insta_media")
public class InstagramMedia {

    @Id
    @Column(name = "pk")
    private long pk;
    @Column(name = "image_url")
    private String image_url;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Transient
    private String captionText = "";
    @Column(name = "temporary_caption", length = 2048)
    private byte[] temporaryCaption;
    @Column(name = "like_count")
    private long like_count;
    @Column(name = "comment_count")
    private long comment_count;
    @Column(name = "media_type")
    private int media_type;
    @Column(name = "block")
    private boolean block = false;
    @Column(name = "user_name")
    private String userName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Transient
    private String fullName = "";
    @Column(name = "temporary_full_name")
    private byte[] temporaryFullName;
    @Column(name = "comment_list")
    private ArrayList<String> commentList;

    public long getPk() {
        return pk;
    }

    public void setPk(long pk) {
        this.pk = pk;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getCaptionText() {
        String str = "";
        try {
            str = new String(temporaryCaption, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    public void setCaptionText(String captionText) {
        this.captionText = captionText;
    }

    public long getLike_count() {
        return like_count;
    }

    public void setLike_count(long like_count) {
        this.like_count = like_count;
    }

    public long getComment_count() {
        return comment_count;
    }

    public void setComment_count(long comment_count) {
        this.comment_count = comment_count;
    }

    public int getMedia_type() {
        return media_type;
    }

    public void setMedia_type(int media_type) {
        this.media_type = media_type;
    }

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        String str = "";
        try {
            str = new String(temporaryFullName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public byte[] getTemporaryCaption() {
        return temporaryCaption;
    }

    public void setTemporaryCaption() {
        if (captionText.length() < 255) {
            temporaryCaption = captionText.getBytes();
        } else {
            temporaryCaption = captionText.substring(0, 255).getBytes();
        }

    }

    public byte[] getTemporaryFullName() {
        return temporaryFullName;
    }

    public void setTemporaryFullName() {
        if (fullName.length() < 255) {
            temporaryFullName = fullName.getBytes();
        } else {
            temporaryFullName = fullName.substring(0, 255).getBytes();
        }

    }

    public ArrayList<String> getCommentList() {
        return commentList;
    }

    public void setCommentList(ArrayList<String> commentList) {
        this.commentList = commentList;
    }

    @Override
    public String toString() {
        return "InstagramMedia{" +
                "pk=" + pk +
                ", image_url='" + image_url + '\'' +
                ", captionText='" + captionText + '\'' +
                ", temporaryCaption=" + Arrays.toString(temporaryCaption) +
                ", like_count=" + like_count +
                ", comment_count=" + comment_count +
                ", media_type=" + media_type +
                ", block=" + block +
                ", userName='" + userName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", temporaryFullName=" + Arrays.toString(temporaryFullName) +
                ", commentList=" + commentList +
                '}';
    }
}

