package com.studio.rai.live2d2;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.audiofx.Visualizer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.studio.rai.live2d2.live2d.L2DModelSetting;
import com.studio.rai.live2d2.live2d.MyL2DModel;

import org.json.JSONException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import jp.live2d.Live2D;

public class MainActivity extends Activity
{
    private static final String TAG = MainActivity.class.getSimpleName();

    private GLSurfaceView mGlSurfaceView;
    private EditText et;

    private Live2DRender mLive2DRender;
    private L2DModelSetting mModelSetting;
    private MyL2DModel mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        Live2D.init();
        initView();



    }

    private void initView() {
        mGlSurfaceView = (GLSurfaceView) findViewById(R.id.main_glSurface);
        et = (EditText) findViewById(R.id.main_et);

        setupLive2DModels();
        mGlSurfaceView.setRenderer(mLive2DRender);
    }

    private void setupLive2DModels() {
        try {
            //String modelName = "izumi_illust";
            String modelName = "hibiki";
            mModelSetting = new L2DModelSetting(this, modelName);
            mModel = new MyL2DModel(this, mModelSetting);

            mLive2DRender = new Live2DRender();
            mLive2DRender.setModel(mModel);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void motions(View view) {
        final String[] keys = mModelSetting.getMotions().keySet().toArray(new String[]{});

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Motions")
                .setItems(keys, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mModel.showMotion(which, keys[which]);
                    }
                })
                .show();
    }

    public void test(View view) {
        String text = et.getText().toString();
        if (text.isEmpty()) return;
        mModel.lipSynch(text);
    }

    private void tts() {
        //String toSpeak = "臣亮言：先帝創業未半，而中道崩殂。今天下三分，益州疲弊，此誠危急存亡之秋也。";
        //Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();


        //t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
        //t1.getVoice().
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();

        if (event.getAction() == MotionEvent.ACTION_MOVE)
            mModel.onTouch(x, y);

        return false;
    }
}
