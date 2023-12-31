package androidapi.model.logutil;

public enum LogType {
    LIKE, COMMENT, FOLLOW,
    LIKE_ORDER, COMMENT_ORDER, FOLLOW_ORDER,
    UNLIKE_GET, UNCOMMENT_GET, UNFOLLOW_GET,
    UNLIKE_MISS, UNCOMMENT_MISS, UNFOLLOW_MISS,
    SEND_DIAMOND, RECIEVE_DIAMOND_FROM_USER, RECIEVE_DIAMOND_FROM_ADMIN,
    GIFT_CODE,
    PURCHASE_DIAMOND,
    INTRODUCER, PRESENTED,
    PRIVATE_MISS,
}
