package customanimation.com.pushwooshandroiddemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.pushwoosh.BasePushMessageReceiver;
import com.pushwoosh.BaseRegistrationReceiver;
import com.pushwoosh.PushManager;


public class MainActivity extends ActionBarActivity {
    TextView mtextView;
    BroadcastReceiver mBroadcastReceiver = new BaseRegistrationReceiver() {
        @Override
        protected void onRegisterActionReceive(Context context, Intent intent) {
            checkMessage(intent);
        }
    };
    BroadcastReceiver mReceiver = new BasePushMessageReceiver() {
        @Override
        protected void onMessageReceive(Intent intent) {
            showMessage("push message is" + intent.getExtras().getString("JSON_KEY"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mtextView = (TextView) findViewById(R.id.textview_notification);
        registerReceiver();
        PushManager pushManager = PushManager.getInstance(this);
        try {
            pushManager.onStartup(this);
        } catch (Exception ex) {

        }
        pushManager.registerForPushNotifications();
        checkMessage(getIntent());

    }

    private void checkMessage(Intent intent) {
        if (intent != null) {
            if (intent.hasExtra(PushManager.PUSH_RECEIVE_EVENT)) {
                showMessage("push message is " + intent.getExtras().getString(PushManager.PUSH_RECEIVE_EVENT));
            } else if (intent.hasExtra(PushManager.REGISTER_EVENT)) {
                showMessage("register");
            } else if (intent.hasExtra(PushManager.UNREGISTER_EVENT)) {
                showMessage("unregister");
            } else if (intent.hasExtra(PushManager.REGISTER_ERROR_EVENT)) {
                showMessage("register error");
            } else if (intent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT)) {
                showMessage("unregister error");
            }

            resetIntentValues();
        }
    }

    private void resetIntentValues() {
        Intent mainAppIntent = getIntent();
        if (mainAppIntent.hasExtra(PushManager.PUSH_RECEIVE_EVENT)) {
            mainAppIntent.removeExtra(PushManager.PUSH_RECEIVE_EVENT);
        } else if (mainAppIntent.hasExtra(PushManager.REGISTER_EVENT)) {
            mainAppIntent.removeExtra(PushManager.REGISTER_EVENT);
        } else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_EVENT)) {
            mainAppIntent.removeExtra(PushManager.UNREGISTER_EVENT);
        } else if (mainAppIntent.hasExtra(PushManager.REGISTER_ERROR_EVENT)) {
            mainAppIntent.removeExtra(PushManager.REGISTER_ERROR_EVENT);
        } else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT)) {
            mainAppIntent.removeExtra(PushManager.UNREGISTER_ERROR_EVENT);
        }

        setIntent(mainAppIntent);
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        mtextView.setText(message);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        checkMessage(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter(getPackageName() + ".action.PUSH_MESSAGE_RECEIVE");

        registerReceiver(mReceiver, intentFilter, getPackageName() + ".permission.C2D_MESSAGE", null);

        registerReceiver(mBroadcastReceiver, new IntentFilter(getPackageName() + "." + PushManager.REGISTER_BROAD_CAST_ACTION));
    }

    public void unRegisterReceiver() {
        try {
            unregisterReceiver(mReceiver);
        } catch (Exception ex) {

        }
        try {
            unregisterReceiver(mBroadcastReceiver);
        } catch (Exception ex) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        unRegisterReceiver();

    }


}
