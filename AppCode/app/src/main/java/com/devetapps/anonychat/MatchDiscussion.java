package com.devetapps.anonychat;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import io.socket.client.Socket;

public class MatchDiscussion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_discussion);

        TextView headerText = (TextView)findViewById(R.id.headerText);
        headerText.setText("Discussion avec " + getIntent().getStringExtra("matchUserName")); //On affiche le pseudo en entête

        Socket mSocket = SocketManager.getInstance(); //On récupère le socket
        mSocket.on("msgReceived", (message) -> { //On affiche (en utilisant le thread principal) le message (quand on en reçoit un)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addMsgToLayout(message[0].toString(), false);
                }
            });
        });

        //Quand le serveur renvoie une erreur depuis le salon de discussion
        mSocket.on("error", (error) -> {
            String errorText = "Erreur : ";
            if(error[0] == "NO_ROOM") {
                errorText += "vous n'êtes dans aucune pièce !";
            }
            Toast errorToast = Toast.makeText(this, errorText, Toast.LENGTH_LONG);
            errorToast.show();
        });

        //Quand l'interlocuteur arrête la discussion
        mSocket.on("endChat", (e) -> {
            finish();
        });
    }

    /*Quand le bouton d'envoi de message est appuyé*/
    public void onMsgSend(View view) {
        Socket mSocket = SocketManager.getInstance();
        EditText msg = (EditText)findViewById(R.id.msgInput);
        mSocket.emit("msgSent", msg.getText().toString());
        addMsgToLayout(msg.getText().toString(), true);
    }

    /*Quand le bouton d'arrêt de la discussion est appuyé*/
    public void onChatEnd(View view) {
        Socket mSocket = SocketManager.getInstance();
        mSocket.emit("endChatRequest");
        finish();
    }

    //Ajoute un texte au layout contenant la liste des messages
    @SuppressLint("UseCompatLoadingForDrawables")
    public void addMsgToLayout(String msg, boolean isSelf) {
        //Récup conteneur message
        LinearLayout msgListLayout = (LinearLayout)findViewById(R.id.msgList);

        //Création/Config layout contenant le nouveau message
        LinearLayout newMsgView = new LinearLayout(this);

        //Paramètres du layout du nouveau message
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = isSelf ? Gravity.RIGHT : Gravity.LEFT;
        newMsgView.setLayoutParams(params);
        newMsgView.setOrientation(LinearLayout.VERTICAL);

        //Création de la vue du TEXTE du nouveau message
        TextView msgToDisplay = new TextView(this);
        msgToDisplay.setTextSize(17);
        msgToDisplay.setTextColor(Color.WHITE);
        msgToDisplay.setPadding(30, 9, 30, 9);

        // Modification de la bulle du nouveau message
        Drawable chatBubbleDrawable = getDrawable(R.drawable.chat_bubble).getConstantState().newDrawable();
        if (chatBubbleDrawable instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) chatBubbleDrawable;
            gradientDrawable.setColor(isSelf ? Color.parseColor("#2C59B3") : Color.parseColor("#111111"));
        } else if (chatBubbleDrawable instanceof ShapeDrawable) {
            ShapeDrawable shapeDrawable = (ShapeDrawable) chatBubbleDrawable;
            shapeDrawable.getPaint().setColor(isSelf ? Color.BLUE : Color.RED);
        }

        //Application de la bulle de message personnalisée
        msgToDisplay.setBackground(chatBubbleDrawable);

        //Ajout du texte
        msgToDisplay.setText(msg);

        //Ajout de la vue du texte dans le layout du message
        newMsgView.addView(msgToDisplay);

        //Ajout du layout du message dans la liste des messages
        msgListLayout.addView(newMsgView);

        //Création/Modification d'un espace, puis ajout dans la liste des messages
        //(Afin de créer une marge entre chaque message
        Space margin = new Space(this);
        margin.setLayoutParams(new LinearLayout.LayoutParams(0, 30));
        msgListLayout.addView(margin);
    }
}