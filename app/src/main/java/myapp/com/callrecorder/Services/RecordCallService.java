package myapp.com.callrecorder.Services;


import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.IBinder;

import java.io.File;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import myapp.com.callrecorder.AppPreferences;
import myapp.com.callrecorder.Database.CallDetails;
import myapp.com.callrecorder.Database.CallLog;
import myapp.com.callrecorder.Database.Customer;
import myapp.com.callrecorder.Database.Database;
import myapp.com.callrecorder.MainActivity;

/**
 * The nitty gritty Service that handles actually recording the conversations
 */

public class RecordCallService extends Service {
    InputStream inputStream = null;
    String result = null;
    String responceStatus;
    int tempstatus, responseUserId;
    public final static String ACTION_START_RECORDING = "com.jlcsoftware.ACTION_CLEAN_UP";
    public final static String ACTION_STOP_RECORDING = "com.jlcsoftware.ACTION_STOP_RECORDING";
    public final static String EXTRA_PHONE_CALL = "com.jlcsoftware.EXTRA_PHONE_CALL";
    List<Customer> custcount;
    int count, CustomerId;
    Database db = new Database(this);
    String CallDetailsMobNo, CallType, AudioFileUploadStatus;
    Calendar StartTime, EndTime;
    String CallDateTime;
    String CallDuration;
    String audiofilename;
    String path;
    String fileName;
    final Handler handler = new Handler();
    final int delay = 10000; //milliseconds
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static boolean HandlerStatus = false;
    //  boolean CurrentHandlerStatus;
    int id;

    //boolean status;
    public RecordCallService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ContentValues parcelableExtra = intent.getParcelableExtra(EXTRA_PHONE_CALL);
        startRecording(new CallLog(parcelableExtra));
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        stopRecording();
        super.onDestroy();
    }

    private CallLog phoneCall;

    boolean isRecording = false;

    private void stopRecording() {

        custcount = db.getAllCustomers();
        count = 0;
        for (Customer cn : custcount) {
            count++;
        }
        if (count > 0) {
            //  new updateLocationDetails().execute();
            Database db = new Database(getApplicationContext());
            Customer user1 = db.getCustomerDetails();
            CustomerId = user1.getCust_id();
        }
        if (isRecording) {
            try {
                phoneCall.setEndTime(Calendar.getInstance());
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = false;

                phoneCall.save(getBaseContext());
                // displayNotification(phoneCall);
                SendCallDetails(phoneCall);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        phoneCall = null;
    }


    MediaRecorder mediaRecorder;


    private void startRecording(CallLog phoneCall) {
        if (!isRecording) {
            isRecording = true;
            this.phoneCall = phoneCall;
            File file = null;
            try {
                this.phoneCall.setSartTime(Calendar.getInstance());
                File dir = AppPreferences.getInstance(getApplicationContext()).getFilesDirectory();
                mediaRecorder = new MediaRecorder();
                file = File.createTempFile("record", ".3gp", dir);
                this.phoneCall.setPathToRecording(file.getAbsolutePath());
                //    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
                //    recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

                mediaRecorder.setAudioSamplingRate(8000);
                mediaRecorder.setAudioEncodingBitRate(12200);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mediaRecorder.setOutputFile(phoneCall.getPathToRecording());
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (Exception e) {
                e.printStackTrace();
                isRecording = false;
                if (file != null) file.delete();
                this.phoneCall = null;
                isRecording = false;
            }
        }
    }

    public void SendCallDetails(CallLog phoneCall) {

        CallDetailsMobNo = phoneCall.getPhoneNumber();
        if (phoneCall.isOutgoing()) {
            CallType = "Outgoing";

        } else {
            CallType = "Incoming";
        }

        StartTime = phoneCall.getStartTime();
        EndTime = phoneCall.getEndTime();

        float time = EndTime.getTimeInMillis() - StartTime.getTimeInMillis();
        CallDuration = (String.format("%.2f", ((time / 1000) / 60)));

        DateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

        CallDateTime = df.format(StartTime.getTime());//14/12/17 1:01 PM to 14-12-2017 1:01:00

        File file = new File(phoneCall.getPathToRecording());

        path = file.toString();
        String[] parts = path.split("/");
        fileName = parts[parts.length - 1];

        AudioFileUploadStatus = "No";
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("CallDetailsMobNo", CallDetailsMobNo);
        editor.apply();

        long Id = db.addCallDetails(new CallDetails(path, CallDetailsMobNo, CallType, CallDuration, CallDateTime, AudioFileUploadStatus));
        editor.putLong("Id", Id);
        editor.apply();

        Intent in = new Intent(RecordCallService.this, MainActivity.class);
        startActivity(in);
    }

    public static void sartRecording(Context context, CallLog phoneCall) {
        Intent intent = new Intent(context, RecordCallService.class);
        intent.setAction(ACTION_START_RECORDING);
        intent.putExtra(EXTRA_PHONE_CALL, phoneCall.getContent());
        context.startService(intent);
    }

    public static void stopRecording(Context context) {
        Intent intent = new Intent(context, RecordCallService.class);
        intent.setAction(ACTION_STOP_RECORDING);
        context.stopService(intent);
    }
}
