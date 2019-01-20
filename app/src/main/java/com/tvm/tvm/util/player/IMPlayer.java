/*
 *
 * IMPlayer.java
 * 
 */
package com.tvm.tvm.util.player;

import java.util.ArrayList;

/**
 * Description:
 */
public interface IMPlayer {

    /**
     * 璁剧疆璧勬簮
     * @param url 璧勬簮璺緞
     * @throws MPlayerException
     */
    void setSource(ArrayList<String> urls, boolean flag) throws MPlayerException;

    /**
     * 璁剧疆鏄剧ず瑙嗛鐨勮浇浣�
     * @param display 瑙嗛鎾斁鐨勮浇浣撳強鐩稿叧鐣岄潰
     */
    void setDisplay(IMDisplay display);

    /**
     * 鎾斁瑙嗛
     * @throws MPlayerException
     */
    void play() throws MPlayerException;

    /**
     * 鏆傚仠瑙嗛
     */
    void pause();


    void onPause();
    void onResume();
    void onDestroy();

}
