package com.devetapps.anonychat;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.net.URISyntaxException;
import java.util.Objects;

import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Socket mSocket = SocketManager.getInstance();

        mSocket.connect();

        mSocket.io().on(Manager.EVENT_TRANSPORT, args -> {
            Transport transport = (Transport) args[0];
            transport.on(Transport.EVENT_ERROR, args1 -> {
                Exception e = (Exception) args1[0];
                Log.e(TAG, "Transport error " + e);
                e.printStackTrace();
                Objects.requireNonNull(e.getCause()).printStackTrace();
            });
        });

        Intent matchActivity = new Intent(getApplicationContext(), MatchDiscussion.class);
        startActivity(matchActivity);



        mSocket.on("match", (user) -> {
           Log.i("SOCKET", "Match avec : " + user[0].toString());
           onMatch(user[0].toString());
        });
    }

    public void onUserConnect(View view) {
        Socket mSocket = SocketManager.getInstance();
        EditText userName = (EditText)findViewById(R.id.userName);
        if(mSocket.connected()) {
            mSocket.emit("beginSearch", userName.getText().toString());
            Log.i("SOCKET", "Pseudo envoyé : " + userName.getText().toString());
        }
    }

    public void onMatch(String matchUserName) {
        Intent matchActivity = new Intent(getApplicationContext(), MatchDiscussion.class);
        matchActivity.putExtra("matchUserName", matchUserName);
        startActivity(matchActivity);
    }
}