package myapp.com.callrecorder;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import myapp.com.callrecorder.Database.Customer;
import myapp.com.callrecorder.Database.Database;

import static myapp.com.callrecorder.Constants.Constants.baseUrlRegistration;
import static myapp.com.callrecorder.Constants.Constants.isNetworkAvailable;
import static myapp.com.callrecorder.Constants.Constants.progressDialog;

public class RegistrationActivity extends AppCompatActivity {
    Button BtnRegister;
    EditText EdtUserName, EdtMobileNumber;
    String AccessToken, UserName, MobNumber;
    String result = null;
    String strStatus;
    int status;
    int responseUserId;
    ProgressDialog progress;
    List<Customer> custcount;
    int count;
    Database db = new Database(this);
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        progress = new ProgressDialog(RegistrationActivity.this);
        EdtUserName = (EditText) findViewById(R.id.username);
        EdtMobileNumber = (EditText) findViewById(R.id.mob_no);
        BtnRegister = (Button) findViewById(R.id.register_button);
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        BtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionRegister();
            }
        });
    }

    public void ActionRegister() {
        UserName = EdtUserName.getText().toString();
        MobNumber = EdtMobileNumber.getText().toString();

        if (isNetworkAvailable(RegistrationActivity.this)) {
            if (UserName == null || UserName.equals("") || MobNumber == null || MobNumber.equals("")) {
                Toast.makeText(getApplicationContext(), "Enter all valid fields", Toast.LENGTH_LONG).show();
            } else if (EdtUserName.getText().toString().trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Enter User Name", Toast.LENGTH_LONG).show();
            } else if (MobNumber.length() < 10) {
                Toast.makeText(getApplicationContext(), "Enter correct Mobile number", Toast.LENGTH_LONG).show();
            } else {

                if (!hasPermissions(this, PERMISSIONS)) {
                    ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
                } else {
                    new SendRegistrationDetails(UserName, MobNumber).execute();
                    progressDialog(progress, "Loading", "Please wait...");
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_LONG).show();
        }
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onStart() {

        super.onStart();

        custcount = db.getAllCustomers();
        count = 0;
        for (Customer cn : custcount) {
            count++;
        }
        if (count > 0) {
//            Intent i = new Intent(RegistrationActivity.this,MainActivity.class);
//            startActivity(i);
            //Code to exit from app
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
    }

    public class SendRegistrationDetails extends AsyncTask<String, Void, String> {
        private final String mUserName;
        private final String mMobileNo;

        SendRegistrationDetails(String username, String mobno) {
            mUserName = username;
            mMobileNo = mobno;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                // code to consume wcf service which sends the details to the server
                String url = baseUrlRegistration + "Register";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("UserName", UserName);
                jsonObject.accumulate("MobileNo", MobNumber);
                jsonObject.accumulate("grant_type", "password");


                json = jsonObject.toString();

                httpPost.setEntity(new StringEntity("grant_type=password&UserName=" + mUserName + "&MobileNo=" + mMobileNo, "UTF-8"));
                // 7. Set some headers to inform server about the type of the content

                httpPost.setHeader("Accept", "application/x-www-form-urlencoded");
                httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");

                HttpResponse response = httpclient.execute(httpPost);

                status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    JSONObject othposjsonobj = new JSONObject(data);
                    //   strStatus = othposjsonobj.getString("Status");
                    //   if (strStatus.equals("success")) {
                    strStatus = othposjsonobj.getString("Status");
                    if (strStatus.equals("success")) {
                        responseUserId = Integer.parseInt(othposjsonobj.getString("UserID"));
                        AccessToken = othposjsonobj.getString("access_token");
                    }

                } else {
                    // result = "Did not work!";
                }
            } catch (Exception e) {
                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                //appendLog(LoginActivity.this, "1 LoginActivity " + e.toString() + date);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            try {

                if (status == 401) {
                    Toast.makeText(getApplicationContext(), "401 Unauthorized!,Please login again.", Toast.LENGTH_LONG).show();
                } else if (status == 503) {
                    Toast.makeText(getApplicationContext(), " 503 Service not available!,Please login again.", Toast.LENGTH_LONG).show();

                } else if (status == 200) {
                    if (strStatus.equals("success")) {

                        db.addCustomer(new Customer(responseUserId, AccessToken, UserName, MobNumber));
//                      Intent i = new Intent(RegistrationActivity.this, MainActivity.class);
//                      startActivity(i);

                        //Code to exit from app
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        startActivity(intent);
                    }
                } else if (strStatus.equals("fail")) {
                    Toast.makeText(RegistrationActivity.this, "Connection Fail!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegistrationActivity.this, "Service not available!,Please login again.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                //appendLog(LoginActivity.this, "3 LoginActivity " + e.toString() + date);
            }
            if (progress.isShowing()) {
                progress.dismiss();
            }
        }
    }
}
