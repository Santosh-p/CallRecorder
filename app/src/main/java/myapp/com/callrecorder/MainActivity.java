package myapp.com.callrecorder;

import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

import myapp.com.callrecorder.Database.Customer;
import myapp.com.callrecorder.Database.Database;
import myapp.com.callrecorder.Services.ProcessTimer;

import static myapp.com.callrecorder.Constants.Constants.baseUrl;
import static myapp.com.callrecorder.Constants.Constants.isNetworkAvailable;
import static myapp.com.callrecorder.Constants.Constants.progressDialog;

public class MainActivity extends AppCompatActivity {
    List<Customer> custcount;
    int count, CustomerId;
    Database db = new Database(this);

    TextView tvCallNo;
    EditText edtName, edtAddress, edtPurpose, edtComments;
    int Callid;
    long Id;
    String AccessToken, CallDetailsMobNo, Name, Address, Purpose, Comments;
    Button btnSubmit;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    int tempstatus;
    InputStream inputStream = null;
    String result, resStatus;
    int status;

    ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFragment(new Fragments());
        tvCallNo = (TextView) findViewById(R.id.tv_currentcall_no);
        edtName = (EditText) findViewById(R.id.edtname);
        edtAddress = (EditText) findViewById(R.id.edtaddress);
        edtPurpose = (EditText) findViewById(R.id.edtpurpose);
        edtComments = (EditText) findViewById(R.id.edtcomments);


        btnSubmit = (Button) findViewById(R.id.register_button);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Name = edtName.getText().toString();
                Address = edtAddress.getText().toString();
                Purpose = edtPurpose.getText().toString();
                Comments = edtComments.getText().toString();

                db.UpdateCallDetails(Id, Name, Address, Purpose, Comments);

                int repeatTime = 1;  //Repeat alarm time in seconds
                AlarmManager processTimer = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent i = new Intent(getApplicationContext(), ProcessTimer.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                processTimer.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), repeatTime * 1000, pendingIntent);

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //  super.onBackPressed();

        //Code to exit from app
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        progress = new ProgressDialog(MainActivity.this);
        Name = "";
        Address = "";
        Purpose = "";
        Comments = "";
        edtName.setText("");
        edtAddress.setText("");
        edtPurpose.setText("");
        edtComments.setText("");
        Database db = new Database(getApplicationContext());
        custcount = db.getAllCustomers();
        count = 0;
        for (Customer cn : custcount) {
            count++;
        }
        if (count > 0) {
            //  new updateLocationDetails().execute();
            Customer user1 = db.getCustomerDetails();
            CustomerId = user1.getCust_id();
            AccessToken = user1.getAccess_token();
        }

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        if (sharedpreferences.contains("CallDetailsMobNo")) {
            CallDetailsMobNo = sharedpreferences.getString("CallDetailsMobNo", CallDetailsMobNo);
        }
        if (sharedpreferences.contains("Id")) {
            Id = sharedpreferences.getLong("Id", Id);
        }
        tvCallNo.setText("Current calls details : " + CallDetailsMobNo);
        if (isNetworkAvailable(MainActivity.this)) {
            new GetCustomerDetails().execute();
            progressDialog(progress, "Loading", "Please wait...");
        } else {
            // Toast.makeText(MainActivity.this, "Check Your Network Connection", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadFragment(Fragments fragment) {
// create a FragmentManager
        FragmentManager fm = getFragmentManager();
// create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
// replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.fragment_miss_call, fragment);
        fragmentTransaction.commit(); // save the changes
    }

    public class GetCustomerDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {

                String url = baseUrl + "GetCallInfo";
                //http://202.88.154.118/RecorderWebAPI/api/Home/GetCallInfo
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";

                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("MobileNo", CallDetailsMobNo);//9561906233

                // 4. convert JSONObject to JSON to String
                json = jsonObject.toString();

                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json);
                se.setContentType("application/json");
                //se.setContentEncoding("bearer "+AccessTocken);
                // 6. set httpPost Entity
                httpPost.setEntity(se);

                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                httpPost.setHeader("Authorization", "bearer " + AccessToken);
//YHCN3eHIQ3_LDi1i-EBo4H1zTD9aSxjndjVWaJ_OtCCjfhjL_8lcfHWLERB74hOc2L1SqfF60vF9NBmsXs_F0Z97pYVAoZVnGdgK-6_H1mx4-Uk5Ks2SciQGos5bReooLKzPDY_u5WKgQBVwJM3UkrWsJIo8l0ZrLgSx_0Xy0Z-1zdbNjOzgYHFIGveW9C94Wa-k4LaXGFi2rueKo5M0vh53jLHgYNgp4CJ7nDYOC4RaPnQo6Utax4edHimuCqpf
                HttpResponse response = httpclient.execute(httpPost);

                // StatusLine stat = response.getStatusLine();
                status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);//

                    JSONArray othposjsonobj = new JSONArray(data);
                    if(othposjsonobj.length()>0){
                    JSONObject jobj = othposjsonobj.getJSONObject(0);
                    resStatus = jobj.getString("Status");
                    if (resStatus.equals("success")) {
                        Name = jobj.getString("Name");
                        Address = jobj.getString("Address");
                        Purpose = jobj.getString("Purpose");
                        Comments = jobj.getString("Comments");
                    }}

                } else {
                    // result = "Did not work!";
                }

            } catch (Exception e) {
//                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
//                String date = df.format(Calendar.getInstance().getTime());
//                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                //appendLog(FieldActivity.this, "4 FieldActivity " + e.toString() + date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (status == 401) {
                Toast.makeText(getApplicationContext(), "401 Unauthorized!,Please try again.", Toast.LENGTH_LONG).show();
            } else if (status == 503) {
                Toast.makeText(getApplicationContext(), "503 Service not available!,Please try again.", Toast.LENGTH_LONG).show();
            } else if (status == 200) {
                if (resStatus.equals("success")) {
                    edtName.setText(Name);
                    edtAddress.setText(Address);
                    edtPurpose.setText(Purpose);
                    edtComments.setText(Comments);
                } else if (resStatus.equals("fail")) {
                    Toast.makeText(getApplicationContext(), "Connection Fail!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Service not available!", Toast.LENGTH_LONG).show();
            }
            if (progress.isShowing()) {
                progress.dismiss();
            }
        }
    }
}
