package ge.bondx.fcgagramessanger;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import ge.bondx.fcgagramessanger.service.JSONHttpClient;
import ge.bondx.fcgagramessanger.service.ServiceUrl;


/**
 * Created by Admin on 4/23/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(refreshedToken);
        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);

    }

    private void sendRegistrationToServer(String token) {
       /* SharedPreferences pref = getSharedPreferences("perf_general",MODE_PRIVATE);
        String username = pref.getString("username", null);
        JSONHttpClient jsonHttpClient = new JSONHttpClient();
        jsonHttpClient.GetService(ServiceUrl.TOKEN + "?user="+username+"&token=" + token);*/
    }
}