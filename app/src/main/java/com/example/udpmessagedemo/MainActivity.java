package com.example.udpmessagedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MainActivityViewModel mainActivityViewModel = new MainActivityViewModel();
    private PermissionHelper permissionHelper;
    private UDPMessageHelper udpMessageHelper;
    private Repository repository = Repository.getInstance();
    private TextView textViewLocalIPAdress, textViewIncomingMessage, textViewServerReceiver,textViewIncomingMessageTitle,textViewOutgoingMessageTitle;
    private EditText editTextHomePort, editTextDestinationPort, editTextOutgoingMessage, editTextDestinationIpAdress;
    private Button buttonStartUDP;
    private ImageButton imageButtonChangePorts,imageButtonRandomNumber;
    private Switch switchServerReceiver;
    private String destinationIpAddress;
    private boolean isEditTextsFilled = false;
    private boolean isServer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissionHelper = new PermissionHelper(this);
        permissionHelper.checkPermissions();
        initViews();
        setOnClickListeners();
        //Get And Update IP Address
        updateIpAdress();
        //Initialize And Start UDP Helper
        udpMessageHelper = new UDPMessageHelper(this);
        setServerReceiverStatus();
    }

    @Override
    public void onClick(View view) {
        if (view == buttonStartUDP) {
            updateIpAdress();
            updateRepository();
            if (isEditTextsFilled) {
                if (!udpMessageHelper.isAlive()){
                    udpMessageHelper.start();
                }
            }
        } else if (view == imageButtonChangePorts) {
           switchPorts();
        } else if (view == switchServerReceiver) {
            switchPorts();
            updateRepository();
            setServerReceiverStatus();

        } else if (view==imageButtonRandomNumber){
            editTextOutgoingMessage.setText(mainActivityViewModel.generateRandomInput());
        }
    }

    private void initViews() {
        textViewLocalIPAdress = findViewById(R.id.textViewLocalIPAdress);
        editTextDestinationIpAdress = findViewById(R.id.editTextDestinationIpAdress);
        textViewIncomingMessage = findViewById(R.id.textViewIncomingMessage);
        editTextHomePort = findViewById(R.id.editTextHomePort);
        editTextDestinationPort = findViewById(R.id.editTextDestinationPort);
        editTextOutgoingMessage = findViewById(R.id.editTextOutgoingMessage);
        buttonStartUDP = findViewById(R.id.buttonStartUDP);
        imageButtonChangePorts = findViewById(R.id.imageButtonChangePorts);
        textViewServerReceiver = findViewById(R.id.textViewServerReceiver);
        switchServerReceiver = findViewById(R.id.switchServerReceiver);
        textViewIncomingMessageTitle=findViewById(R.id.textViewIncomingMessageTitle);
        textViewOutgoingMessageTitle=findViewById(R.id.textViewOutgoingMessageTitle);
        textViewServerReceiver=findViewById(R.id.textViewServerReceiver);
        imageButtonRandomNumber=findViewById(R.id.imageButtonRandomNumber);
    }

    private void setOnClickListeners() {
        buttonStartUDP.setOnClickListener(this);
        imageButtonChangePorts.setOnClickListener(this);
        switchServerReceiver.setOnClickListener(this);
        imageButtonRandomNumber.setOnClickListener(this);
    }

    private void updateRepository() {
        isEditTextsFilled = true;
        if (!editTextHomePort.getText().toString().equals("")) {
            int homePort = Integer.parseInt(editTextHomePort.getText().toString());
            repository.setHomePort(homePort);
        } else {
            isEditTextsFilled = false;
        }
        if (!editTextDestinationPort.getText().toString().equals("")) {
            int destinationPort = Integer.parseInt(editTextDestinationPort.getText().toString());
            repository.setDestinationPort(destinationPort);
        } else {
            isEditTextsFilled = false;
        }
        if (!editTextOutgoingMessage.getText().toString().equals("")) {
            if (editTextOutgoingMessage.getVisibility()==View.VISIBLE){
                byte[] outGoingMessage = mainActivityViewModel.getByteArray(Integer.parseInt(editTextOutgoingMessage.getText().toString()));
                repository.setOutgoingMessage(outGoingMessage);
            }
        } else {
            if (editTextOutgoingMessage.getVisibility()==View.VISIBLE)
            isEditTextsFilled = false;
        }
        if (!editTextDestinationIpAdress.getText().toString().equals("")) {
            repository.setDestinationAddress(editTextDestinationIpAdress.getText().toString());
        }else {
            isEditTextsFilled = false;
        }
        if (!isEditTextsFilled) {
            Toast.makeText(this, "Please make sure to fill all the input boxes", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateIpAdress() {
        if (mainActivityViewModel.getLocalIpAddress() != null) {
            String localIp = mainActivityViewModel.getLocalIpAddress();
            textViewLocalIPAdress.setText(localIp);
            //Set Destination IP Adress Edit Text to local IP to ease input:
            if (editTextDestinationIpAdress.getText().toString().equals("")) {
                editTextDestinationIpAdress.setText(localIp);
            }

            repository.setLocalAddress(localIp);
        }
    }

    private void switchPorts(){
        if (!editTextHomePort.getText().toString().equals("") && !editTextDestinationPort.getText().toString().equals("")) {
            String first = editTextHomePort.getText().toString();
            String second = editTextDestinationPort.getText().toString();
            editTextHomePort.setText(second);
            editTextDestinationPort.setText(first);
        }
    }

    private void setServerReceiverStatus(){
        if (switchServerReceiver.isChecked()) {
            isServer = false;
            textViewOutgoingMessageTitle.setVisibility(View.GONE);
            editTextOutgoingMessage.setVisibility(View.GONE);
            imageButtonRandomNumber.setVisibility(View.GONE);
            textViewIncomingMessageTitle.setVisibility(View.VISIBLE);
            textViewIncomingMessage.setVisibility(View.VISIBLE);
            textViewServerReceiver.setText("Receiver");

        } else {
            isServer = true;
            textViewOutgoingMessageTitle.setVisibility(View.VISIBLE);
            editTextOutgoingMessage.setVisibility(View.VISIBLE);
            imageButtonRandomNumber.setVisibility(View.VISIBLE);
            textViewIncomingMessageTitle.setVisibility(View.GONE);
            textViewIncomingMessage.setVisibility(View.GONE);
            textViewServerReceiver.setText("Server");
        }
        Repository.getInstance().setServer(isServer);
    }

    public TextView getTextViewIncomingMessage() {
        return textViewIncomingMessage;
    }
}
