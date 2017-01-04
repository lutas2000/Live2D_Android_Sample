package com.studio.rai.live2d2;

import android.app.Activity;
import android.content.DialogInterface;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.studio.rai.live2d2.live2d.MyL2DModel;

import jp.live2d.Live2D;

public class MainActivity extends Activity
{
    private static final String TAG = MainActivity.class.getSimpleName();

    private Live2DRender mLive2DRender;
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
        GLSurfaceView glSurfaceView = (GLSurfaceView) findViewById(R.id.main_glSurface);

        setupLive2DModels();
        glSurfaceView.setRenderer(mLive2DRender);
    }

    private void setupLive2DModels() {
        mModel = new MyL2DModel(this);
        mModel.setupModel("hibiki/hibiki.moc", "hibiki/hibiki.2048");
        mModel.setupMotions("hibiki/hibiki.physics.json", "hibiki/motions");

        mLive2DRender = new Live2DRender();
        mLive2DRender.addModel(mModel);
    }

    public void motions(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Motions")
                .setItems(mModel.getMotions(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mModel.showMotion(which);
                    }
                })
                .show();
    }

    float angle = -30f;
    public void test(View view) {
        /*
        Log.d(TAG, angle+"");
        mModel.test(angle);
        angle++;*/
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();

        //Log.d(TAG, "x " + x);
        //Log.d(TAG, "y " + y);

        if (event.getAction() == MotionEvent.ACTION_MOVE)
            mModel.onTouch(x, y);

        return false;
    }
}
