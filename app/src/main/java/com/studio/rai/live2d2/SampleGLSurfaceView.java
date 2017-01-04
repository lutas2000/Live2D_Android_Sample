package com.studio.rai.live2d2;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by LUTAS on 2017/1/3.
 */

public class SampleGLSurfaceView extends GLSurfaceView
{
    public SampleGLSurfaceView(Context context, Renderer renderer) {
        super(context);
        setRenderer(renderer) ;
    }
}
