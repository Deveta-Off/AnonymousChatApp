package com.devetapps.anonychat;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.net.URISyntaxException;
import java.util.Objects;

import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;

public class MainActivity extends AppCompatActivity {
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://10.243.204.79:3000");
        } catch (URISyntaxException e) {
            System.out.println("Erreur : " + e);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    }
}