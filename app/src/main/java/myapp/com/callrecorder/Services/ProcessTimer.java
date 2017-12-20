package myapp.com.callrecorder.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import myapp.com.callrecorder.Database.CallDetails;
import myapp.com.callrecorder.Database.Customer;
import myapp.com.callrecorder.Database.Database;

import static android.content.Context.ALARM_SERVICE;
import static myapp.com.callrecorder.Constants.Constants.baseUrl;
import static myapp.com.callrecorder.Constants.Constants.baseUrlUploadFile;
import static myapp.com.callrecorder.Constants.Constants.isNetworkAvailable;


/**
 * Created by SSPL on 12/08/17.
 */

public class ProcessTimer extends BroadcastReceiver {
    List<Customer> custcount;
    List<CallDetails> custCallcount;
    int count, calldetailscount, CustomerId;
    int id;
    String CallDetailsMobNo, CallType, AudioFileUploadStatus, Name, Address, Purpose, Comments;
    String CallDateTime;
    String CallDuration;
    String AccessToken;
    String path;
    String fileName;
    InputStream inputStream = null;
    String result = null;
    String responceStatus, resStatus;
    int tempstatus, status;
    private Context context;


    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        // this.eventId= eventId;
        Toast.makeText(context, "Timer Starts", Toast.LENGTH_LONG).show();
        Database db = new Database(context);
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
        custCallcount = db.getAllCustomersCalldetails();
        calldetailscount = 0;
        for (CallDetails cn : custCallcount) {
            calldetailscount++;
        }
        if (calldetailscount > 0) {
            List<CallDetails> calldetailscount = db.getAllCustomersCalldetails();
            int callcount = 0;
            for (CallDetails cn : calldetailscount) {
                id = cn.getId();
                path = cn.getFile_path();
                CallDetailsMobNo = cn.getCalldetails_mobno();
                CallType = cn.getCall_type();
                CallDuration = cn.getCall_duration();
                CallDateTime = cn.getCall_datetime();
                AudioFileUploadStatus = cn.getFile_upload_status();
                Name = cn.getName();
                Address = cn.getAddress();
                Purpose = cn.getPurpose();
                Comments = cn.getComments();

                if (isNetworkAvailable(context)) {
                    new SendCallRecordingDetails().execute();
                    uploadFilee(path);
                } else {
                    Toast.makeText(context, "Check Network connection", Toast.LENGTH_LONG).show();
                }
                callcount++;
            }
        } else {
            //Cancel the alarm
            Intent i = new Intent(context, ProcessTimer.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, i, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            Toast.makeText(context, "Timer Stops", Toast.LENGTH_LONG).show();
        }
    }

    //send call details to server
    public class SendCallRecordingDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                if (Name == null) {
                    Name = "";
                }
                if (Address == null) {
                    Address = "";
                }
                if (Purpose == null) {
                    Purpose = "";
                }
                if (Comments == null) {
                    Comments = "";
                }
                String url = baseUrl + "SendCallDetails";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("UserID", CustomerId);
                jsonObject.accumulate("MobileNo", CallDetailsMobNo);
                jsonObject.accumulate("CallType", CallType);
                jsonObject.accumulate("CallDuration", CallDuration);
                jsonObject.accumulate("CallDateTime", CallDateTime);
                jsonObject.accumulate("UploadStatus", AudioFileUploadStatus);
                jsonObject.accumulate("Name", Name);
                jsonObject.accumulate("Address", Address);
                jsonObject.accumulate("Purpose", Purpose);
                jsonObject.accumulate("Comments", Comments);


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
                    JSONObject jobj = othposjsonobj.getJSONObject(0);
                    resStatus = jobj.getString("Status");

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
                Toast.makeText(context, "401 Unauthorized!,Please try again.", Toast.LENGTH_LONG).show();
            } else if (status == 503) {
                Toast.makeText(context, "503 Service not available!,Please try again.", Toast.LENGTH_LONG).show();
            } else if (status == 200) {
                if (resStatus.equals("success")) {
                    Toast.makeText(context, "Call details sends", Toast.LENGTH_LONG).show();
                    Database db = new Database(context);
                    db.deleteCallDetails(id);
                } else if (resStatus.equals("fail")) {
                    Toast.makeText(context, "Connection Fail!", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(context, "Service not available!", Toast.LENGTH_LONG).show();
            }

        }
    }
    //upload file to server
    public int uploadFilee(final String selectedFilePath) {
        int serverResponseCode = 0;
        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);

        String[] parts = selectedFilePath.split("/");
        fileName = parts[parts.length - 1];

        if (!selectedFile.isFile()) {
            // Toast.makeText(getApplicationContext(), "File not found.", Toast.LENGTH_SHORT).show();
            return 0;
        } else {
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(baseUrlUploadFile + CustomerId);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("Authorization", "bearer " + AccessToken);
                connection.setRequestProperty("UploadAudioFile", selectedFilePath);
                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());
                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"UploadAudioFile\";filename=\""
                        + fileName + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);
                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0) {
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();


                InputStream is = connection.getInputStream();
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();
                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);
                is.close();
                try {
                    JSONObject job = new JSONObject(responseStrBuilder.toString());
                    String Status = job.getString("Key");
                    String UploadedFileResponce = job.getString("Value");
                    if (Status.equals("success")) {
                        boolean deleted = selectedFile.delete();
                        if (deleted) {
                            Toast.makeText(context, "File deleted successfully.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                // Toast.makeText(MainActivity.this,"File Not Found",Toast.LENGTH_SHORT).show();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                //  Toast.makeText(MainActivity.this, "URL error!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                //   Toast.makeText(MainActivity.this, "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();
            }
            return serverResponseCode;
        }
    }

}
