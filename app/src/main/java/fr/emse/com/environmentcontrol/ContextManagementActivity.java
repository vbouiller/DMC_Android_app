package fr.emse.com.environmentcontrol;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

import fr.emse.com.environmentcontrol.Model.RoomContextRule;
import fr.emse.com.environmentcontrol.Model.RoomContextState;
import fr.emse.com.environmentcontrol.Model.Status;
import fr.emse.com.environmentcontrol.networking.RoomContextHttpManager;

public class ContextManagementActivity extends AppCompatActivity {

    String room;
    RoomContextState roomContextState;
    ArrayList<RoomContextRule> rules = new ArrayList<>();
    public Parcelable.Creator<RoomContextRule> CREATOR = new Parcelable.Creator<RoomContextRule>()
    {
        public RoomContextRule createFromParcel(Parcel in)
        {
            int description=in.readInt();
            Serializable s=in.readSerializable();
            return (RoomContextRule) s;
        }

        public RoomContextRule[] newArray(int size)
        {
            return new RoomContextRule[size];
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_context_management);

        //Définition d'une règle
        RoomContextRule rule1 = new RoomContextRule() {
            @Override
            public boolean condition(RoomContextState roomContextState) {
                return roomContextState.getLight() >= 50 && roomContextState.getRinger() > 1.0;
            }

            @Override
            public void action() {
                //si le téléphone n'est pas en silencieux
                if(((AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE)).getRingerMode() != AudioManager.RINGER_MODE_SILENT){
                    //Si on a la permission et si le téléphone n'est pas en silencieux, on le met
                    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ((NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE)).isNotificationPolicyAccessGranted())) {
                        ((AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE)).setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        Toast.makeText(getApplicationContext(), this.getName() + " appliquée!", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getApplicationContext(), "Mise en Silencieux non autorisée - "+this.getName()+" non appliquée!", Toast.LENGTH_LONG).show();
                } else{
                    Toast.makeText(getApplicationContext(), "Téléphone déjà en mode silencieux - " + this.getName() + " non appliquée!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {
                parcel.writeInt(this.describeContents());
                parcel.writeSerializable(this);
            }
        };
        rule1.setName("Rule 1");
        rule1.setDescription("If light level >= 100 && noise level > 1.0 => Phone silent mode activated");

        rules.add(rule1);

        RoomContextRule rule2 = new RoomContextRule() {
            @Override
            public boolean condition(RoomContextState roomContextState) {
                return !(roomContextState.getLight() >= 50 && roomContextState.getRinger() > 1.0);
            }

            @Override
            public void action() {
               //si le téléphone est en silencieux, on l'enlève
                if (((AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE)).getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
                    //Si on a la permission on l'enlève
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ((NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE)).isNotificationPolicyAccessGranted()) {
                        ((AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE)).setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        Toast.makeText(getApplicationContext(), this.getName() + " appliquée!", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getApplicationContext(), "Mise en mode normal non autorisée - " + this.getName() + " non appliquée!", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Téléphone déjà en mode normal - " + this.getName() + " non appliquée!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {
                parcel.writeInt(this.describeContents());
                parcel.writeSerializable(this);
            }

        };
        rule2.setName("Rule 2");
        rule2.setDescription("Si Rule 1 n'est pas appliqué, alors on passe le téléphone en mode normal");

        rules.add(rule2);

        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public ArrayList<RoomContextRule> getRules() {
        return rules;
    }

    //When we receive a contextState, we update everything we have to
    public void onUpdate(RoomContextState context){
        roomContextState = context;
        updateContextView();
        updateRuleApplicaton();
        System.out.println(context.toString());
    }

    private void updateContextView(){
        if (this.roomContextState != null){
            ((TextView) findViewById(R.id.textViewLightValue)).setText(Integer.toString(roomContextState.getLight()));
            ((TextView) findViewById(R.id.textViewRingerValue)).setText(Float.toString(roomContextState.getRinger()));
            ((Button) findViewById(R.id.switch_light_button)).setEnabled(true);
            ((Button) findViewById(R.id.switch_ringer_button)).setEnabled(true);

            if (roomContextState.getLightStatus() == Status.ON){
                ((ImageView) findViewById(R.id.image_bulb_on)).setVisibility(View.VISIBLE);
                ((ImageView) findViewById(R.id.image_bulb_off)).setVisibility(View.INVISIBLE);
            } else {
                ((ImageView) findViewById(R.id.image_bulb_off)).setVisibility(View.VISIBLE);
                ((ImageView) findViewById(R.id.image_bulb_on)).setVisibility(View.INVISIBLE);
            }

            if (roomContextState.getRingerStatus() == Status.ON){
                ((ImageView) findViewById(R.id.image_son_on)).setVisibility(View.VISIBLE);
                ((ImageView) findViewById(R.id.image_son_off)).setVisibility(View.INVISIBLE);
            } else {
                ((ImageView) findViewById(R.id.image_son_off)).setVisibility(View.VISIBLE);
                ((ImageView) findViewById(R.id.image_son_on)).setVisibility(View.INVISIBLE);
            }

        } else {
            initView();
        }
    }

    private void updateRuleApplicaton(){
       for (RoomContextRule rule : rules){
           if(rule.condition(roomContextState))
               rule.action();
       }
    }

    private void initView(){
        // At first we don't have any roomContext, so the buttons are not enabled
        ((Button) findViewById(R.id.switch_light_button)).setEnabled(false);
        ((Button) findViewById(R.id.switch_ringer_button)).setEnabled(false);

        //Listener for room check button
        ((Button) findViewById(R.id.buttonCheck)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                room = ((EditText) findViewById(R.id.editText1))
                        .getText().toString();
                retrieveRoomContextState(room);
            }
        });

        //Listener for the Switch Light button
        ((Button) findViewById(R.id.switch_light_button)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switchLight(roomContextState);
            }
        });

        //Listener for the Switch Ringer button
        ((Button) findViewById(R.id.switch_ringer_button)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switchRoomRinger(roomContextState);
            }
        });

        //Listener for the Switch Phone Ringer button
        ((Button) findViewById(R.id.switch_phone_ringer_button)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    switchPhoneRinger();
            }
        });

        //Listener for the List rules button
        ((Button) findViewById(R.id.list_rules_button)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = null;
                try {


                    intent = new Intent(ContextManagementActivity.this, Class.forName("fr.emse.com.environmentcontrol.ListRules"));
                    //intent.putParcelableArrayListExtra("fr.emse.com.environmentcontrol.RULES",rules);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                ContextManagementActivity.this.startActivity(intent);
            }
        });


    }

    //Method which calls the RoomContextHttpManager method to send a GET request and retrieve the context of a room
    protected void retrieveRoomContextState(String room){
        RoomContextHttpManager.retrieveRoomContextState(room,this);
    }

    //Method which calls the RoomContextHttpManager method to send a POST request to switch light
    protected void switchLight(RoomContextState roomContextState){
        RoomContextHttpManager.switchLight(roomContextState, this);
    }

    //Method which calls the RoomContextHttpManager method to send a POST request to switch ringer
    protected void switchRoomRinger(RoomContextState roomContextState){
        RoomContextHttpManager.switchRoomRinger(roomContextState, this);
    }

    public void switchPhoneRinger(){
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (ContextCompat.checkSelfPermission(ContextManagementActivity.this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(ContextManagementActivity.this, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted()) {
                System.out.println("POLICY ACCESS: "+notificationManager.isNotificationPolicyAccessGranted());
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Permission nécessaire", Toast.LENGTH_SHORT).show();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && notificationManager.isNotificationPolicyAccessGranted()) {
                AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                int mode = audioManager.getRingerMode();
                if (mode == AudioManager.RINGER_MODE_SILENT)
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                else {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                }
            }


        } else {
            Toast.makeText(getApplicationContext(), "Problème de permissions", Toast.LENGTH_SHORT).show();
        }


    }

}
