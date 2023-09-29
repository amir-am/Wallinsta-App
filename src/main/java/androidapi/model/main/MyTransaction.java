package androidapi.model.main;

import androidapi.model.logutil.LogType;

/**
 * Created by Amir Hossein on 8/13/2017.
 */
public class MyTransaction {
    private int diamond;
    private int change;
    private LogType type;

    public MyTransaction() {
    }

    public MyTransaction(int diamond, int change, LogType type) {
        this.diamond = diamond;
        this.change = change;
        this.type = type;
    }

    public int getDiamond() {
        return diamond;
    }

    public void setDiamond(int diamond) {
        this.diamond = diamond;
    }

    public int getChange() {
        return change;
    }

    public void setChange(int change) {
        this.change = change;
    }

    public LogType getType() {
        return type;
    }

    public void setType(LogType type) {
        this.type = type;
    }
}

