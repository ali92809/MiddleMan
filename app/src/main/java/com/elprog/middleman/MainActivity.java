package com.elprog.middleman;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity  {
    private ServerSocket serverSocket;
    private Socket tempClientSocket;
    Thread serverThread = null;
    public static final int SERVER_PORT = 3003;



MyReceiver myReceiver;
Socket clint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter("com.elprog.Emitter_INTENT");
        registerReceiver(myReceiver, filter);

        this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();

    }
    public void broadcastIntent(String  s) {
        Intent intent = new Intent();
        intent.setAction("com.elprog.Middelman_INTENT");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra("response",s);
        sendBroadcast(intent);
    }







        private void sendMessage(final String message) {
            try {
                if (null != tempClientSocket) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            PrintWriter out = null;
                            try {
                                out = new PrintWriter(new BufferedWriter(
                                        new OutputStreamWriter(tempClientSocket.getOutputStream())),
                                        true);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            out.println(message);
                        }
                    }).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        class ServerThread implements Runnable {

            public void run() {
                Socket socket;
                try {
                    serverSocket = new ServerSocket(SERVER_PORT);
                   // findViewById(R.id.start_server).setVisibility(View.GONE);
                } catch (IOException e) {
                    e.printStackTrace();
                    //showMessage("Error Starting Server : " + e.getMessage(), Color.RED);
                }
                if (null != serverSocket) {
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            socket = serverSocket.accept();
                            CommunicationThread commThread = new CommunicationThread(socket);
                            new Thread(commThread).start();
                        } catch (IOException e) {
                            e.printStackTrace();
                          //  showMessage("Error Communicating to Client :" + e.getMessage(), Color.RED);
                        }
                    }
                }
            }
        }

        class CommunicationThread implements Runnable {

            private Socket clientSocket;

            private BufferedReader input;

            public CommunicationThread(Socket clientSocket) {
                this.clientSocket = clientSocket;
                tempClientSocket = clientSocket;
                try {
                    this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                    //showMessage("Error Connecting to Client!!", Color.RED);
                }
                //showMessage("Connected to Client!!", greenColor);
            }

            public void run() {

                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        String read = input.readLine();
                        if (null == read || "Disconnect".contentEquals(read)) {
                            Thread.interrupted();
                            read = "Client Disconnected";
                           // showMessage("Client : " + read, greenColor);
                            break;
                        }
                        broadcastIntent(read);
                        //showMessage("Client : " + read, greenColor);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

        }



    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {



            serverThread = new Thread(new ServerThread());
            serverThread.start();

             String struser=intent.getStringExtra("userData");

            sendMessage(struser);



        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);

        if (null != serverThread) {
            sendMessage("Disconnect");
            serverThread.interrupt();
            serverThread = null;
        }
    }
}