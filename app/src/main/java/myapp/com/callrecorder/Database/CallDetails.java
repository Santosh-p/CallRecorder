package myapp.com.callrecorder.Database;

/**
 * Created by SSPL on 12/06/17.
 */

public class CallDetails {
    int id;
    String file_path, calldetails_mobno, call_type, call_duration, call_datetime, file_upload_status,name,address,purpose,comments;

    public CallDetails() {
    }

    public CallDetails(String file_path, String calldetails_mobno, String call_type, String call_duration, String call_datetime, String file_upload_status) {
        this.file_path = file_path;
        this.calldetails_mobno = calldetails_mobno;
        this.call_type = call_type;
        this.call_duration = call_duration;
        this.call_datetime = call_datetime;
        this.file_upload_status = file_upload_status;
    }

    public CallDetails(int id, String file_path, String calldetails_mobno, String call_type, String call_duration, String call_datetime, String file_upload_status) {
        this.id = id;
        this.file_path = file_path;
        this.calldetails_mobno = calldetails_mobno;
        this.call_type = call_type;
        this.call_duration = call_duration;
        this.call_datetime = call_datetime;
        this.file_upload_status = file_upload_status;
    }

    public CallDetails(int id, String file_path, String calldetails_mobno, String call_type, String call_duration, String call_datetime, String file_upload_status, String name, String address, String purpose, String comments) {
        this.id = id;
        this.file_path = file_path;
        this.calldetails_mobno = calldetails_mobno;
        this.call_type = call_type;
        this.call_duration = call_duration;
        this.call_datetime = call_datetime;
        this.file_upload_status = file_upload_status;
        this.name = name;
        this.address = address;
        this.purpose = purpose;
        this.comments = comments;
    }

    public CallDetails(String file_path, String calldetails_mobno, String call_type, String call_duration, String call_datetime, String file_upload_status, String name, String address, String purpose, String comments) {
        this.file_path = file_path;
        this.calldetails_mobno = calldetails_mobno;
        this.call_type = call_type;
        this.call_duration = call_duration;
        this.call_datetime = call_datetime;
        this.file_upload_status = file_upload_status;
        this.name = name;
        this.address = address;
        this.purpose = purpose;
        this.comments = comments;
    }

    public CallDetails(String name, String address, String purpose, String comments) {
        this.name = name;
        this.address = address;
        this.purpose = purpose;
        this.comments = comments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getCalldetails_mobno() {
        return calldetails_mobno;
    }

    public void setCalldetails_mobno(String calldetails_mobno) {
        this.calldetails_mobno = calldetails_mobno;
    }

    public String getCall_type() {
        return call_type;
    }

    public void setCall_type(String call_type) {
        this.call_type = call_type;
    }

    public String getCall_duration() {
        return call_duration;
    }

    public void setCall_duration(String call_duration) {
        this.call_duration = call_duration;
    }

    public String getCall_datetime() {
        return call_datetime;
    }

    public void setCall_datetime(String call_datetime) {
        this.call_datetime = call_datetime;
    }

    public String getFile_upload_status() {
        return file_upload_status;
    }

    public void setFile_upload_status(String file_upload_status) {
        this.file_upload_status = file_upload_status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
