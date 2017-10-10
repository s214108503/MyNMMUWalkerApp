package walker.pack.firebaseclasses;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by s214108503 on 2017/09/18.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static  final String REG_TOKEN = "REG_TOKEN";
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String recent_token = FirebaseInstanceId.getInstance().getToken();
        Log.i(REG_TOKEN, recent_token);

    }
}
