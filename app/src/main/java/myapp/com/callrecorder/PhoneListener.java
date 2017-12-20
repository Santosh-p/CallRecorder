package myapp.com.callrecorder;

import android.content.Context;
import android.os.AsyncTask;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import myapp.com.callrecorder.Database.CallLog;
import myapp.com.callrecorder.Database.Customer;
import myapp.com.callrecorder.Database.Database;
import myapp.com.callrecorder.Database.MissCallDetails;
import myapp.com.callrecorder.Services.RecordCallService;

import static myapp.com.callrecorder.Constants.Constants.baseUrl;
import static myapp.com.callrecorder.Constants.Constants.isNetworkAvailable;

/**
 * Created by Jeff on 01-May-16.
 * <p/>
 * The logic is a little odd here...
 * <p/>
 * When a incoming call comes in, we get a CALL_STATE_RINGING that provides the incoming number and all is easy and good...
 * on the other hand, a Outgoing call generates a ACTION_NEW_OUTGOING_CALL with the phone number, then an a CALL_STATE_IDLE and then a
 * CALL_STATE_OFFHOOK when the call connects - we never get the outgoing number in the PhoneState Change
 * <p/>
 */
public class PhoneListener extends PhoneStateListener {
    ArrayList<Integer> alImage;
    int tempstatus;
    InputStream inputStream = null;
    String missCallDetailsMobNo, Name, AccessToken, resStatus;
    int status;
    List<Customer> custcount;
    int count, CustomerId;
    private static PhoneListener instance = null;
    private static boolean ring = false;
    private static boolean callReceived = false;
    // Database db = new Database(context);

    /**
     * Must be called once on app startup
     *
     * @param context - application context
     * @return
     */
    public static PhoneListener getInstance(Context context) {
        if (instance == null) {
            instance = new PhoneListener(context);
        }
        return instance;
    }

    public static boolean hasInstance() {
        return null != instance;
    }

    private final Context context;
    private CallLog phoneCall;
    String MissCallDateTime;

    private PhoneListener(Context context) {
        this.context = context;
    }

    AtomicBoolean isRecording = new AtomicBoolean();
    AtomicBoolean isWhitelisted = new AtomicBoolean();


    public void setOutgoing(String phoneNumber) {
        if (null == phoneCall)
            phoneCall = new CallLog();
        phoneCall.setPhoneNumber(phoneNumber);
        phoneCall.setOutgoing();
        // called here so as not to miss recording part of the conversation in TelephonyManager.CALL_STATE_OFFHOOK
        // isWhitelisted.set(Database.isWhitelisted(context, phoneCall.getPhoneNumber()));
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        Database db = new Database(context);
        custcount = db.getAllCustomers();
        count = 0;
        for (Customer cn : custcount) {
            count++;
        }
        if (count > 0) {
            //  new updateLocationDetails().execute();
            Customer user1 = db.getCustomerDetails();
            //    CustomerId = user1.getCust_id();
            AccessToken = user1.getAccess_token();
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE: // Idle... no call
                if (ring == true && callReceived == false) {

                    if (!incomingNumber.equals("")) {
                        missCallDetailsMobNo = incomingNumber;
                        Toast.makeText(context, "It was A MISSED CALL from : " + incomingNumber, Toast.LENGTH_LONG).show();
                        //  Intent in = new Intent(context, MainActivity.class);
                        // context.startActivity(in);

                        if (isNetworkAvailable(context.getApplicationContext())) {
                            new GetCustomerDetails().execute();
                            //   progressDialog(progress, "Loading", "Please wait...");
                        } else {
                            // Toast.makeText(MainActivity.this, "Check Your Network Connection", Toast.LENGTH_SHORT).show();
//                            Database db = new Database(context);
                            DateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                            Date currentTime = Calendar.getInstance().getTime();
                            Name = "";
                            MissCallDateTime = df.format(currentTime);
                            long Id = db.addMissCallDetails(new MissCallDetails(Name, missCallDetailsMobNo, MissCallDateTime));

                        }
                    }
                }
                callReceived = false;
                if (isRecording.get()) {
                    RecordCallService.stopRecording(context);
                    phoneCall = null;
                    isRecording.set(false);
                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK: // Call answered
                callReceived = true;
                if (isWhitelisted.get()) {
                    isWhitelisted.set(false);
                    return;
                }
                if (!isRecording.get()) {
                    isRecording.set(true);
                    // start: Probably not ever usefull
                    if (null == phoneCall)
                        phoneCall = new CallLog();
                    if (!incomingNumber.isEmpty()) {
                        phoneCall.setPhoneNumber(incomingNumber);
                    }
                    // end: Probably not ever usefull
                    RecordCallService.sartRecording(context, phoneCall);
                }
                break;
            case TelephonyManager.CALL_STATE_RINGING: // Phone ringing
                ring = true;
                // DO NOT try RECORDING here! Leads to VERY poor quality recordings
                // I think something is not fully settled with the Incoming phone call when we get CALL_STATE_RINGING
                // a "SystemClock.sleep(1000);" in the code will allow the incoming call to stabilize and produce a good recording...(as proof of above)
                if (null == phoneCall)
                    phoneCall = new CallLog();
                if (!incomingNumber.isEmpty()) {
                    phoneCall.setPhoneNumber(incomingNumber);
                    // called here so as not to miss recording part of the conversation in TelephonyManager.CALL_STATE_OFFHOOK
                    //isWhitelisted.set(Database.isWhitelisted(context, phoneCall.getPhoneNumber()));
                }
                break;
        }
    }


    public class GetCustomerDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {

                String url = baseUrl + "GetCallInfo";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";


                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("MobileNo", missCallDetailsMobNo);//

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


                HttpResponse response = httpclient.execute(httpPost);

                // StatusLine stat = response.getStatusLine();
                status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);//


                    JSONArray othposjsonobj = new JSONArray(data);
                    if (othposjsonobj.length() > 0) {
                        JSONObject jobj = othposjsonobj.getJSONObject(0);
                        resStatus = jobj.getString("Status");
                        if (resStatus.equals("success")) {
                            Name = jobj.getString("Name");
//                            Address = jobj.getString("Address");
//                            Purpose = jobj.getString("Purpose");
//                            Comments = jobj.getString("Comments");
                        }
                    }

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
                //   Toast.makeText(getApplicationContext(), "401 Unauthorized!,Please try again.", Toast.LENGTH_LONG).show();
            } else if (status == 503) {
                //     Toast.makeText(getApplicationContext(), "503 Service not available!,Please try again.", Toast.LENGTH_LONG).show();
            } else if (status == 200) {
                if (resStatus.equals("success")) {
                    if (Name == null || Name.equals("") || Name.isEmpty()) {
                        Name = "";
                    }
                    Database db = new Database(context);
                    DateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                    Date currentTime = Calendar.getInstance().getTime();

                    MissCallDateTime = df.format(currentTime);
                    long Id = db.addMissCallDetails(new MissCallDetails(Name, missCallDetailsMobNo, MissCallDateTime));

                } else if (resStatus.equals("fail")) {
                    Toast.makeText(context, "Connection Fail!", Toast.LENGTH_SHORT).show();
                }
            } else {
                //Toast.makeText(getApplicationContext(), "Service not available!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
