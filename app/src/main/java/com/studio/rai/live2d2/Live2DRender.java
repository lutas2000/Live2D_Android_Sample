package com.studio.rai.live2d2;

import android.opengl.GLSurfaceView;

import com.studio.rai.live2d2.live2d.MyL2DModel;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by LUTAS on 2017/1/3.
 */

public class Live2DRender implements GLSurfaceView.Renderer
{
    private int mModelIndex = 0;
    private ArrayList<MyL2DModel> mModelManagers;

    public Live2DRender() {
        mModelManagers = new ArrayList<>();
    }

    public void addModel(MyL2DModel modelManager) {
        mModelManagers.add(modelManager);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mModelManagers.get(mModelIndex).onSurfaceCreated(gl);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mModelManagers.get(mModelIndex).onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mModelManagers.get(mModelIndex).onDrawFrame(gl);
    }
}