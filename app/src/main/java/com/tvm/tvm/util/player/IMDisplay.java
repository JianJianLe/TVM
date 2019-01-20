/*
 *
 * IMDisplay.java
 * 
 */
package com.tvm.tvm.util.player;

import android.view.SurfaceHolder;
import android.view.View;

/**
 * Description:
 */
public interface IMDisplay extends IMPlayListener {

    View getDisplayView();
    SurfaceHolder getHolder();

}
