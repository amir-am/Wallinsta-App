package androidapi.model.packages;

import androidapi.model.packages.OrderPackage;

import java.util.List;

/**
 * Created by Amir Hossein on 8/5/2017.
 */
public class PackageList {
    private List<OrderPackage> likeList;
    private List<OrderPackage> commentList;
    private List<OrderPackage> followerList;

    public void setLikeList(List likeList) {
        this.likeList = likeList;
    }

    public void setCommentList(List commentList) {
        this.commentList = commentList;
    }

    public void setFollowerList(List followerList) {
        this.followerList = followerList;
    }

    public List<OrderPackage> getLikeList() {
        return likeList;
    }

    public List<OrderPackage> getCommentList() {
        return commentList;
    }

    public List<OrderPackage> getFollowerList() {
        return followerList;
    }
}
