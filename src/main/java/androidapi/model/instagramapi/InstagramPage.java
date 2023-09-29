package androidapi.model.instagramapi;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by Amir Hossein on 7/23/2017.
 */

@Entity
@Table(name = "insta_page")
public class InstagramPage {
    @Column(name = "username")
    private String username;
    @Id
    @Column(name = "pk")
    private long pk;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Transient
    private String full_name = "";
    @Column(name = "temporary_full_name")
    private byte[] temporaryFull_name;
    @Column(name = "is_private")
    private boolean is_private;
    @Column(name = "profile_pic_url")
    private String profilePictureURI;
    @Column(name = "block")
    private boolean block = false;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getPk() {
        return pk;
    }

    public void setPk(long pk) {
        this.pk = pk;
    }

    public String getFull_name() {
        String str = "";
        try {
            str = new String(temporaryFull_name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public boolean isIs_private() {
        return is_private;
    }

    public void setIs_private(boolean is_private) {
        this.is_private = is_private;
    }

    public String getProfilePictureURI() {
        return profilePictureURI;
    }

    public void setProfilePictureURI(String profilePictureURI) {
        this.profilePictureURI = profilePictureURI;
    }

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    public byte[] getTemporaryFull_name() {
        return temporaryFull_name;
    }

    public void setTemporaryFull_name() {
        if (full_name.length() < 255) {
            temporaryFull_name = full_name.getBytes();
        } else {
            temporaryFull_name = full_name.substring(0, 255).getBytes();
        }
    }

    @Override
    public String toString() {
        return "InstagramPage{" +
                "username='" + username + '\'' +
                ", pk=" + pk +
                ", full_name='" + full_name + '\'' +
                ", temporaryFull_name=" + Arrays.toString(temporaryFull_name) +
                ", is_private=" + is_private +
                ", profilePictureURI='" + profilePictureURI + '\'' +
                ", block=" + block +
                '}';
    }
}
