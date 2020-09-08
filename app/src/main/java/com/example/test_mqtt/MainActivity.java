package com.example.test_mqtt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MqttAndroidClient client;
    String topic;
    private RecyclerView rcChat;
    private EditText edtChat;
    List<User> listChat = new ArrayList<>();
    private Button btnOk;
    private static final String USER_NAME = Build.DEVICE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rcChat = findViewById(R.id.rcChat);
        edtChat = findViewById(R.id.edtChat);
        topic = "chat";
        client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.hivemq.com:1883",
                        USER_NAME);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    sub();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("TAG", "onFailure");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void setupRecyclerView() {
        final ChatAdapter chatAdapter = new ChatAdapter(listChat, this);
        rcChat.setAdapter(chatAdapter);
        LinearLayoutManager vertical = new LinearLayoutManager(this);
        rcChat.setLayoutManager(vertical);
        rcChat.smoothScrollToPosition(listChat.size()-1);
    }

    private void publicMessage() {
        String chat= edtChat.getText().toString().trim();
        String payload="" + USER_NAME + ": " + chat ;
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            message.setRetained(true);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

    private void sub() {
        if(client.isConnected()) {
            try {
                client.subscribe(topic, 0);
                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        String all_message = new String(message.getPayload());
                        String message2 = all_message.substring(all_message.indexOf(":") + 1);
                        String user_name = String.valueOf(all_message.startsWith(USER_NAME));

                        User user = new User(user_name, message2);
                        listChat.add(user);
                        setupRecyclerView();
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public void Ok(View view) {
        publicMessage();
        cleaText();
    }

    private void cleaText(){
        edtChat.setText(null);
    }


}