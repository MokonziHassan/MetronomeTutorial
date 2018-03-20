package com.isen.khass.metronometutorial;

        import android.annotation.TargetApi;
        import android.content.Context;
        import android.graphics.Color;
        import android.media.AudioManager;
        import android.os.AsyncTask;
        import android.os.Build;
        import android.os.Handler;
        import android.os.Message;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.KeyEvent;
        import android.view.View;
        import android.view.View.OnLongClickListener;
        import android.widget.AdapterView;
        import android.widget.AdapterView.OnItemSelectedListener;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.SeekBar;
        import android.widget.SeekBar.OnSeekBarChangeListener;
        import android.widget.Spinner;
        import android.widget.TextView;

        import java.lang.annotation.Target;

public class MetronomeActivity extends AppCompatActivity {

    private final short minBpm = 40;
    private final short maxBpm = 208;
    private short bpm = 100;
    private short noteValue = 4;
    private short beats = 4;
    private short volume;
    private short initialvolume;
    private double beatSound =  2440;
    private double sound = 6440;
    private AudioManager audio;

    private MetronomeAsyncTask metroTask;

    private Button plusButton;
    private Button minusButton;
    private TextView currentBeat;
    private Handler mHandle;


    private Handler getHandler(){
        return new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String message = (String)msg.obj;
                if(message.equals("1"))
                    currentBeat.setTextColor(Color.GREEN);
                else
                    currentBeat.setTextColor(getResources().getColor(R.color.yellow));
                currentBeat.setText(message);
            }
        };
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        metroTask = new MetronomeAsyncTask();
        //Set values and listeners to buttons and stuff

        TextView bpmText = (TextView) findViewById(R.id.bps);
        bpmText.setText(""+bpm);

        TextView timeSignatureText = (TextView) findViewById(R.id.timesignature);
        timeSignatureText.setText(""+beats+"/"+noteValue);

        plusButton = (Button) findViewById(R.id.plus);
        plusButton.setOnLongClickListener(plusListener);

        minusButton = (Button) findViewById(R.id.minus);
        minusButton.setOnLongClickListener( minusListener);

        currentBeat = (TextView) findViewById(R.id.currentBeat);
        currentBeat.setTextColor(Color.GREEN);

        Spinner beatSpinner = (Spinner) findViewById(R.id.beatspinner);
        ArrayAdapter<Beats> arrayBeats = new ArrayAdapter<Beats>(this,
                android.R.layout.simple_spinner_item, Beats.values());
        beatSpinner.setAdapter(arrayBeats);
        beatSpinner.setSelection(Beats.quatre.ordinal());
        arrayBeats.setDropDownViewResource(R.layout.activity_spinner_dropdown);
        beatSpinner.setOnItemSelectedListener(beatSpinnerListener);

        Spinner noteValuesdSpinner = (Spinner) findViewById(R.id.notespinner);
        ArrayAdapter<NoteValues> noteValues = new ArrayAdapter<NoteValues>(this,
                android.R.layout.simple_spinner_item, NoteValues.values());
        noteValuesdSpinner.setAdapter(noteValues);
        noteValues.setDropDownViewResource(R.layout.activity_spinner_dropdown);
        noteValuesdSpinner.setOnItemSelectedListener(noteValueSpinnerListener);
        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        initialvolume = (short) audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        volume = initialvolume;

        SeekBar volumebar = (SeekBar) findViewById(R.id.volumebar);
        volumebar.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumebar.setProgress(volume);
        volumebar.setOnSeekBarChangeListener(volumeListener);
    }

    public synchronized void onStartStopClick(View view) {
        Button button = (Button) view;
        String buttonText = button.getText().toString();
        if(buttonText.equalsIgnoreCase("start")){
            button.setText(R.string.stop);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                metroTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        (Void[])null);
            else
                metroTask.execute();
        }
        else {
            button.setText(R.string.start);
            metroTask.stop();
            metroTask = new MetronomeAsyncTask();
            Runtime.getRuntime().gc();
        }
    }

    private void maxBpmGuard() {
        if(bpm >= maxBpm) {
            plusButton.setEnabled(false);
            plusButton.setPressed(false);
        } else if(!minusButton.isEnabled() && bpm>minBpm) {
            minusButton.setEnabled(true);
        }
    }

    public void onPlusClick(View view){
        bpm++;
        TextView bpmText = (TextView) findViewById(R.id.bps);
        bpmText.setText(""+bpm);
        metroTask.setBpm(bpm);
        maxBpmGuard();
    }

    private OnLongClickListener plusListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            bpm+=20;
            if(bpm>=maxBpm)
                bpm = maxBpm;
            TextView bpmText = (TextView) findViewById(R.id.bps);
            bpmText.setText(""+bpm);
            metroTask.setBpm(bpm);
            maxBpmGuard();
            return true;
        }
    };

    private  void minBpmGuard() {
        if(bpm<= minBpm){
            minusButton.setEnabled(false);
            minusButton.setPressed(false);
        }else if(!plusButton.isEnabled() && bpm<maxBpm){
            plusButton.setEnabled(true);
        }
    }

    public void onMinusClick(View view){
        bpm--;
        TextView bpmText = (TextView) findViewById(R.id.bps);
        bpmText.setText(""+bpm);
        metroTask.setBpm(bpm);
        minBpmGuard();
    }

    private OnLongClickListener minusListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            bpm-=20;
            if(bpm <= minBpm)
                bpm = minBpm;
            TextView bpmText = (TextView) findViewById(R.id.bps);
            bpmText.setText(""+bpm);
            metroTask.setBpm(bpm);
            minBpmGuard();
            return true;
        }
    };

    private OnSeekBarChangeListener volumeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            volume = (short) progress;
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private OnItemSelectedListener noteValueSpinnerListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            NoteValues notevalue = (NoteValues) adapterView.getItemAtPosition(i);
            TextView timeSignature = (TextView) findViewById(R.id.timesignature);
            timeSignature.setText(""+beats+"/"+notevalue);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private OnItemSelectedListener beatSpinnerListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Beats beat = (Beats) adapterView.getItemAtPosition(i);
            TextView timeSignature = (TextView) findViewById(R.id.timesignature);
            timeSignature.setText(""+"/"+noteValue);
            metroTask.setBeat(beat.getNum());
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent e){
        SeekBar volumebar = (SeekBar) findViewById(R.id.volumebar);
        volume = (short) audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        switch (keyCode){
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                volumebar.setProgress(volume);
                break;
        }
        return super.onKeyUp(keyCode, e);
    }

    public void onBackPressed(){
        metroTask.stop();
        Runtime.getRuntime().gc();
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, initialvolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        finish();
    }

    private class MetronomeAsyncTask extends AsyncTask<Void, Void, String> {

        Metronome metronome;

        MetronomeAsyncTask(){
            mHandle = getHandler();
            metronome = new Metronome(mHandle);
        }


        @Override
        protected String doInBackground(Void... voids) {
            metronome.setBeat(beats);
            metronome.setNoteValue(noteValue);
            metronome.setBpm(bpm);
            metronome.setBeatSound(beatSound);
            metronome.setSound(sound);

            metronome.play();

            return null;
        }

        public void stop(){
            metronome.stop();
            metronome = null;
        }

        public void setBpm(short bpm){
            metronome.setBpm(bpm);
            metronome.calcSilence();
        }

        public void setBeat(short beat){
            if(metronome != null)
                metronome.setBeat(beat);
        }
    }

}
