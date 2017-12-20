package myapp.com.callrecorder.Database;

/**
 * Created by SSPL on 12/01/17.
 */

public class Customer {
    int cust_id;
    String access_token,cust_name, mob_no;

    public Customer() {
    }

    public Customer(int cust_id, String access_token, String cust_name, String mob_no) {
        this.cust_id = cust_id;
        this.access_token = access_token;
        this.cust_name = cust_name;
        this.mob_no = mob_no;
    }

    public int getCust_id() {
        return cust_id;
    }

    public void setCust_id(int cust_id) {
        this.cust_id = cust_id;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    public String getMob_no() {
        return mob_no;
    }

    public void setMob_no(String mob_no) {
        this.mob_no = mob_no;
    }
}
