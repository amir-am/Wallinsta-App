package androidapi.model.main;

public class Initial { private int diamondPerLike;
  private int diamondPerComment;
  private int diamondPerFollow;
  private int diamondMissingPerUnlike;
  private int diamondMissingPerUncomment;
  private int diamondMissingPerUnfollow;
  private int supportDeadline;
  private int diamondMissPerTransfer;
  private String securityKey;
  private String telegramLink;
  private String instagramLink;
  private String aboutUsLink;
  
  public Initial() {}
  
  public String getAboutUsLink() { return aboutUsLink; }
  
  public void setAboutUsLink(String aboutUsLink)
  {
    this.aboutUsLink = aboutUsLink;
  }
  
  public String getTelegramLink() {
    return telegramLink;
  }
  
  public void setTelegramLink(String telegramLink) {
    this.telegramLink = telegramLink;
  }
  
  public String getInstagramLink() {
    return instagramLink;
  }
  
  public void setInstagramLink(String instagramLink) {
    this.instagramLink = instagramLink;
  }
  
  public int getDiamondPerLike() {
    return diamondPerLike;
  }
  
  public void setDiamondPerLike(int diamondPerLike) {
    this.diamondPerLike = diamondPerLike;
  }
  
  public int getDiamondPerComment() {
    return diamondPerComment;
  }
  
  public void setDiamondPerComment(int diamondPerComment) {
    this.diamondPerComment = diamondPerComment;
  }
  
  public int getDiamondPerFollow() {
    return diamondPerFollow;
  }
  
  public void setDiamondPerFollow(int diamondPerFollow) {
    this.diamondPerFollow = diamondPerFollow;
  }
  
  public int getDiamondMissingPerUnlike() {
    return diamondMissingPerUnlike;
  }
  
  public void setDiamondMissingPerUnlike(int diamondMissingPerUnlike) {
    this.diamondMissingPerUnlike = diamondMissingPerUnlike;
  }
  
  public int getDiamondMissingPerUncomment() {
    return diamondMissingPerUncomment;
  }
  
  public void setDiamondMissingPerUncomment(int diamondMissingPerUncomment) {
    this.diamondMissingPerUncomment = diamondMissingPerUncomment;
  }
  
  public int getDiamondMissingPerUnfollow() {
    return diamondMissingPerUnfollow;
  }
  
  public void setDiamondMissingPerUnfollow(int diamondMissingPerUnfollow) {
    this.diamondMissingPerUnfollow = diamondMissingPerUnfollow;
  }
  
  public int getSupportDeadline() {
    return supportDeadline;
  }
  
  public void setSupportDeadline(int supportDeadline) {
    this.supportDeadline = supportDeadline;
  }
  
  public int getDiamondMissPerTransfer() {
    return diamondMissPerTransfer;
  }
  
  public void setDiamondMissPerTransfer(int diamondMissPerTransfer) {
    this.diamondMissPerTransfer = diamondMissPerTransfer;
  }
  
  public String getSecurityKey() {
    return securityKey;
  }
  
  public void setSecurityKey(String securityKey) {
    this.securityKey = securityKey;
  }
}
