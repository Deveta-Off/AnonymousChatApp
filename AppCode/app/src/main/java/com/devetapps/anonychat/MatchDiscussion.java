package com.devetapps.anonychat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.socket.client.Socket;

public class MatchDiscussion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_discussion);
        Socket mSocket = SocketManager.getInstance();
        mSocket.on("msgReceived", (message) -> {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addMsgToLayout(message[0].toString(), false);
                }
            });
        });
    }

    public void onMsgSend(View view) {
        Socket mSocket = SocketManager.getInstance();
        EditText msg = (EditText)findViewById(R.id.msgInput);
        mSocket.emit("msgSent", msg.getText().toString());
        addMsgToLayout(msg.getText().toString(), true);
    }

    //Ajoute un texte au layout contenant la liste des messages
    public void addMsgToLayout(String msg, boolean isSelf) {
        LinearLayout msgListLayout = (LinearLayout)findViewById(R.id.msgList);

        LinearLayout msgView = new LinearLayout(this);
        msgView.setOrientation(LinearLayout.VERTICAL);
        msgListLayout.addView(msgView);

        //msgView.setBackgroundResource(R.drawable.bubble);
        msgView.setPadding(20, 0, 20, 0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.gravity = isSelf ? Gravity.RIGHT : Gravity.LEFT;
        msgView.setLayoutParams(params);


        TextView msgToDisplay = new TextView(this);
        msgToDisplay.setTextSize(25);
        msgToDisplay.setText(msg);

        msgView.addView(msgToDisplay);
    }
}