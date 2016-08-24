package cc.yamyam.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import cc.yamyam.BaseActivity;
import cc.yamyam.R;
import cc.yamyam.Utils;
import cc.yamyam.api.HTTPRequest;
import cc.yamyam.api.HTTPRequestListener;
import cc.yamyam.general.Constants;

public class RegisterActivity extends BaseActivity {

    private static TextView register_label_nickname;
    private static EditText register_nickname;
    private static Button btn_doRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

         this.register_nickname = (EditText)this.findViewById(R.id.register_nickname);
         this.register_label_nickname = (TextView)this.findViewById(R.id.register_label_nickname);
         this.btn_doRegister = (Button)this.findViewById(R.id.btn_doRegister);


        String user_id = Utils.getGlobalValue(this,Constants.USER_ID);
        if(user_id!=null){
            String nickname = Utils.getGlobalValue(this,Constants.USER_NICKNAME);
            doRegisterConfirm(user_id,nickname);
        }

    }

    /*
    private void showRegister(boolean b) {
        if (b) {
            this.register_handynr.setVisibility(View.VISIBLE);
            this.label_handy.setVisibility(View.VISIBLE);
            this.btn_doRegister.setVisibility(View.VISIBLE);
        } else
            this.register_handynr.setVisibility(View.GONE);
        this.label_handy.setVisibility(View.GONE);
        this.btn_doRegister.setVisibility(View.GONE);
    }


    private void showRegisterConfirm(boolean b) {
        if (b) {
            this.register_pwd.setVisibility(View.VISIBLE);
            this.label_confirm .setVisibility(View.VISIBLE);
            this.btn_doRegisterConfirm.setVisibility(View.VISIBLE);
        } else
            this.register_pwd.setVisibility(View.GONE);
            this.label_confirm.setVisibility(View.GONE);
            this.btn_doRegisterConfirm.setVisibility(View.GONE);
    }
    */
    public void doRegister(View view) {
        Log.i(Constants.TAG, "doRegister");
        HTTPRequest request = new HTTPRequest("users/register");
        request.addPostValue("nickname", register_nickname.getText().toString());
        request.setListener(new HTTPRequestListener() {
            @Override
            public void onHTTPRequestFinished(JSONObject response, int tag) {

                try {
                    if (response.getString("code").equals("OK")) {
                        String user_id = response.getString("user_id");
                        Utils.setGlobalValue(getApplicationContext(), Constants.USER_ID, user_id);
                        Utils.setGlobalValue(getApplicationContext(), Constants.USER_NICKNAME, register_nickname.getText().toString());
                        doRegisterConfirm(user_id,register_nickname.getText().toString());
                    } else {

                        fail(response);
                    }
                } catch (Exception e) {
                    fail(null);
                }

            }

            @Override
            public void onHTTPRequestFailed(int tag) {
                fail(null);
            }
        });
        request.start();
    }


    private void doRegisterConfirm(String userid, String nickname)
    {

        this.register_nickname.setVisibility(View.GONE);
        this.btn_doRegister.setVisibility(View.GONE);
        this.register_label_nickname.setText("Welcome " + nickname + "! [ ID:" + userid+"]");

    }
    private void fail(Object o) {

        Toast.makeText(this, "Nickname exist , pls choose another one.", Toast.LENGTH_SHORT).show();
    }

    private void success(){
        Toast.makeText(this, "RegisterActivity Success!!!", Toast.LENGTH_SHORT).show();
    }

    private void successRegister(){

        Toast.makeText(this, "Confirm code Please", Toast.LENGTH_LONG).show();
    }

}
