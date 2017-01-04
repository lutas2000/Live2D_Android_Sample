package com.studio.rai.live2d2.live2d;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import jp.live2d.android.Live2DModelAndroid;
import jp.live2d.android.UtOpenGL;
import jp.live2d.motion.Live2DMotion;
import jp.live2d.motion.MotionQueueManager;

/**
 * Created by LUTAS on 2017/1/3.
 */

public class MyL2DModel
{
    private static final String TAG = MyL2DModel.class.getSimpleName();

    private Context context;
    private AssetManager assetManager;

    private L2DModelSetting mSetting;
    //model
    private Live2DModelAndroid mLive2DModel;
    //motion
    private Live2DMotion[] mMotions;
    private MotionQueueManager mMotionMgr;
    private L2DPhysics physics;
    //motion onTouch
    private float scaleX;
    private float scaleY;
    private final float minAngle = -30f;
    private final float maxAngle = 30f;
    //Sound

    public MyL2DModel(Context context, L2DModelSetting setting) {
        this.context = context;
        mSetting = setting;
        assetManager = context.getAssets();
        mMotionMgr = new MotionQueueManager();

        setupModel();
        setupPhysics();
        setupMotions();
    }

    private void setupModel() {
        try {
            InputStream in = context.getAssets().open(mSetting.getModel()) ;
            mLive2DModel = Live2DModelAndroid.loadModel(in) ;
            in.close() ;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupPhysics() {
        try {
            InputStream in = context.getAssets().open( mSetting.getPhysics() ) ;
            physics = L2DPhysics.load(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupMotions() {
        try {
            Map<String,String> motionPaths = mSetting.getMotions();
            mMotions = new Live2DMotion[motionPaths.size()];

            Iterator<String> iterator = motionPaths.keySet().iterator();
            int count = 0;
            while (iterator.hasNext()) {
                String key = iterator.next();
                InputStream in = context.getAssets().open( motionPaths.get(key) ) ;
                mMotions[count] = Live2DMotion.loadMotion( in ) ;
                in.close() ;
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //========================== Public Method =====================================================

    public void showMotion(int index) {
        if (mMotionMgr.isFinished())
            mMotionMgr.startMotion(mMotions[index], false);
    }

    public void test(float angle) {
        mLive2DModel.setParamFloat( "PARAM_ANGLE_Y", angle ,1 );
    }

    public void onTouch(int x, int y) {
        float angleX = x * scaleX - maxAngle;
        float angleY = -y * scaleY + maxAngle;

        mLive2DModel.setParamFloat( "PARAM_ANGLE_X", angleX ,1 );
        mLive2DModel.setParamFloat( "PARAM_ANGLE_Y", angleY ,1 );
    }

    //========================= SurfaceView Method =================================================

    public void onSurfaceCreated(GL10 gl) {
        setupTexure(gl);
    }

    private void setupTexure(GL10 gl) {
        try {
            String[] texures = mSetting.getTexures();

            for (int i=0; i<texures.length ; i++) {
                InputStream tin = context.getAssets().open(texures[i]) ;
                int texNo = UtOpenGL.loadTexture(gl , tin , true) ;
                //Log.d(TAG, i+" "+texNo);
                mLive2DModel.setTexture(i , texNo) ;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        initAngle(width, height);
        gl.glViewport(0 , 0 , width , height) ;

        gl.glMatrixMode(GL10.GL_PROJECTION) ;
        gl.glLoadIdentity() ;

        float modelWidth = mLive2DModel.getCanvasWidth();
        float visibleWidth = modelWidth * (3.0f/4.0f);
        float margin = 0.5f * (modelWidth/4.0f) ;

        gl.glOrthof(margin, margin+visibleWidth, visibleWidth*height/width, 0, 0.5f, -0.5f);
    }

    private void initAngle(int width, int height) {
        scaleX = (maxAngle-minAngle) / width;
        scaleY = (maxAngle-minAngle) / height;
    }

    public void onDrawFrame(GL10 gl) {
        gl.glMatrixMode(GL10.GL_MODELVIEW) ;
        gl.glLoadIdentity() ;
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT) ;
        gl.glEnable(GL10.GL_BLEND) ;
        gl.glBlendFunc(GL10.GL_ONE , GL10.GL_ONE_MINUS_SRC_ALPHA) ;
        gl.glDisable(GL10.GL_DEPTH_TEST) ;
        gl.glDisable(GL10.GL_CULL_FACE) ;

        if(!mMotionMgr.isFinished())
            mMotionMgr.updateParam(mLive2DModel);

        mLive2DModel.setGL(gl) ;

        mLive2DModel.update();
        mLive2DModel.draw();
    }
}
