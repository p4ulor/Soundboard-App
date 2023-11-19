package xfam0usx.soundboard;

import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.Random;

import xfam0usx.soundboard.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static int buttonsInEachRow = 3;
    private final static int maxSets = 5;
    private MediaPlayer player;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private int currentSet = 1;
    public LinkedList<MyButton> buttons = new LinkedList();

    private ImageView img;

    ActivityMainBinding layout /*(binding)*/ = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = ActivityMainBinding.inflate(getLayoutInflater()); //alternative to findViewById()
        img = findViewById(R.id.imageView2);
        radioGroup = findViewById(R.id.radioGroup);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setButtons();
        refreshEnabledOrDisabled();
    }

    /**
     * Dynamically sets the audios for the buttons, and establishes the number of audios found,
     * so that when the set is changed, the buttons that dont have a number of audios up to the current set, are disabled
     * The audios are obtained from the raw folder, according to the name given to the id's of the buttons
     * so the audios must have the same name, or a number appended to it, up to maxSets (5)
     */
    private void setButtons(){
        Resources r = this.getResources();
        LinearLayout mainLayout = layout.mainLinearLayout;
        int childCount = mainLayout.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View view = mainLayout.getChildAt(i);
            if (view instanceof LinearLayout) {
                LinearLayout linLayout = (LinearLayout) view;
                if(linLayout.getChildCount()==buttonsInEachRow){ //if its an expected layout with 3 buttons
                    for(int j = 0; j < buttonsInEachRow; j++){
                        Button b = (Button) findViewById(linLayout.getChildAt(j).getId()); //getChildAt wasn't working appropriately idk why, the set clicks weren't taking effect, so I used findViewById
                        if(b==null) {
                            log("Error: button is null");
                            continue;
                        }
                        //split to get the name from "xfam0usx.soundboard:id/bear"
                        String buttonName = b.getResources().getResourceName(b.getId()).split("/")[1].toLowerCase(); //https://stackoverflow.com/a/18067589;
                        log("Setting up buttonName="+buttonName);
                        final LinkedList<Integer> audios = new LinkedList<>();

                        for(int x = 0; x < maxSets; x++){ //Get audio file for button with same name pattern as the ID of the button
                            int possibleAudioFile = 0;
                            int possibleAudioFileNumbered;
                            if(x==0){
                                possibleAudioFile = r.getIdentifier(buttonName, "raw", this.getPackageName()); //https://stackoverflow.com/a/11921150
                            }

                            possibleAudioFileNumbered = r.getIdentifier(buttonName+(x+1), "raw", this.getPackageName());

                            if(possibleAudioFile!=0){
                                audios.add(possibleAudioFile);
                            }
                            if(possibleAudioFileNumbered!=0){
                                audios.add(possibleAudioFileNumbered);
                            }
                        }

                        log("Audios found = "+audios.size());
                        if(!audios.isEmpty()){
                            b.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    img.setBackgroundColor(Color.argb(255, getRandom(255), getRandom(255), getRandom(255)));
                                    log("onClick currentSet="+currentSet);
                                    stopPlayer();
                                    try {
                                        int audio = audios.get(currentSet-1);
                                        play(audio);
                                    } catch(IndexOutOfBoundsException e){
                                        log("IndexOutOfBoundsException "+e);
                                    }
                                }
                            });
                        } else {
                            log("Note: no audios for "+buttonName);
                        }
                        buttons.add(new MyButton(b.getId(), audios.size()));
                    }
                }
            }
        }
    }

    /**
     * Called by the radio buttons (click listener set on the activity_main.xml, just to make it different)
     */
    public void checkButton(View v){
        int radioid = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioid);
        Toast.makeText(this, "Selected " + radioButton.getText(), Toast.LENGTH_SHORT).show();
        CharSequence c = radioButton.getContentDescription();
        char cha = c.charAt(0);
        currentSet = cha - '0';
        refreshEnabledOrDisabled();
    }

    private void refreshEnabledOrDisabled(){
        for(int i = 0; i<buttons.size();i++){
            MyButton b = buttons.get(i);
            if(b.getNumOfSets() >= currentSet){
                findViewById(b.getId()).setEnabled(true);
            } else {
                findViewById(b.getId()).setEnabled(false);
            }
        }
    }

    public void play(int id){
        if (player == null) {
            player = MediaPlayer.create(this, id);
        }
        player.start();
    }

    public void pause() {
        if (player != null) {
            player.pause();
        }
    }

    private void stopPlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }

    private void log(String s) {
        Log.i("", s);
    }

    private int getRandom(int upTo) {
        return new Random().nextInt(upTo);
    }
}
