package com.devetapps.anonychat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.socket.client.Socket;

public class MatchDiscussion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_discussion);
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
        TextView msgToDisplay = new TextView(this);
        msgToDisplay.setText(msg);
        msgListLayout.addView(msgToDisplay);
    }
}