package com.studio.rai.live2d2.live2d;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

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

    //model
    private Live2DModelAndroid mLive2DModel;
    private String mModelPath;
    private String mTextureDirPath;
    private String[] mTexturePaths;
    //motion
    private String[] mMotionList;
    private Live2DMotion[] mMotions;
    private MotionQueueManager mMotionMgr;
    private L2DPhysics physics;
    //motion onTouch
    private float scaleX;
    private float scaleY;
    private final float minAngle = -30f;
    private final float maxAngle = 30f;
    //Sound


    public MyL2DModel(Context context) {
        this.context = context;
        assetManager = context.getAssets();
        mMotionMgr = new MotionQueueManager();
    }

    public void setupModel(String modelPath, String textureDirPath) {
        mModelPath = modelPath;
        mTextureDirPath = textureDirPath;
        try {
            mTexturePaths = assetManager.list(textureDirPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setupMotions(String physicsPath, String motionDirPath) {
        setupPhysics(physicsPath);

        try {
            mMotionList = context.getAssets().list(motionDirPath);
            mMotions = new Live2DMotion[mMotionList.length];

            for (int i=0;i<mMotions.length;i++) {
                InputStream in = context.getAssets().open( motionDirPath+ "/"+ mMotionList[i] ) ;
                mMotions[i] = Live2DMotion.loadMotion( in ) ;
                in.close() ;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupPhysics(String physicsPath) {
        try {
            InputStream in = context.getAssets().open( physicsPath ) ;
            physics = L2DPhysics.load(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showMotion(int index) {
        if (mMotionMgr.isFinished())
            mMotionMgr.startMotion(mMotions[index], false);
    }

    public String[] getMotions() {
        return mMotionList;
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
        setupModel(gl);
    }

    private void setupModel(GL10 gl) {
        try
        {
            InputStream in = context.getAssets().open(mModelPath) ;
            mLive2DModel = Live2DModelAndroid.loadModel(in) ;
            in.close() ;

            for (int i = 0; i < mTexturePaths.length ; i++)
            {
                InputStream tin = context.getAssets().open(mTextureDirPath +"/"+ mTexturePaths[i]) ;
                int texNo = UtOpenGL.loadTexture(gl , tin , true) ;
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
