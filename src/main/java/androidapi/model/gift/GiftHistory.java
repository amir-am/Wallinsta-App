package androidapi.model.gift;

import java.util.Calendar;

public class GiftHistory { private String code;
  private Calendar useDate;
  private int diamond;
  
  public GiftHistory() {}
  
  public String getCode() { return code; }
  
  public void setCode(String code)
  {
    this.code = code;
  }
  
  public Calendar getUseDate() {
    return useDate;
  }
  
  public void setUseDate(Calendar useDate) {
    this.useDate = useDate;
  }
  
  public int getDiamond() {
    return diamond;
  }
  
  public void setDiamond(int diamond) {
    this.diamond = diamond;
  }
}
