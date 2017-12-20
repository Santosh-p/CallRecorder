package myapp.com.callrecorder.Database;

/**
 * Created by SSPL on 12/18/17.
 */

public class MissCallDetails {

    int id;
    String misscallDetailName, misscallDetailMobno, misscallDateTime;

    public MissCallDetails() {
    }

    public MissCallDetails(int id, String misscallDetailName, String misscallDetailMobno, String misscallDateTime) {
        this.id = id;
        this.misscallDetailName = misscallDetailName;
        this.misscallDetailMobno = misscallDetailMobno;
        this.misscallDateTime = misscallDateTime;
    }

    public MissCallDetails(String misscallDetailName, String misscallDetailMobno, String misscallDateTime) {
        this.misscallDetailName = misscallDetailName;
        this.misscallDetailMobno = misscallDetailMobno;
        this.misscallDateTime = misscallDateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMisscallDetailName() {
        return misscallDetailName;
    }

    public void setMisscallDetailName(String misscallDetailName) {
        this.misscallDetailName = misscallDetailName;
    }

    public String getMisscallDetailMobno() {
        return misscallDetailMobno;
    }

    public void setMisscallDetailMobno(String misscallDetailMobno) {
        this.misscallDetailMobno = misscallDetailMobno;
    }

    public String getMisscallDateTime() {
        return misscallDateTime;
    }

    public void setMisscallDateTime(String misscallDateTime) {
        this.misscallDateTime = misscallDateTime;
    }
}