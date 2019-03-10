/*
 *
 * MPlayer.java
 * 
 */
package com.tvm.tvm.util.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Description:
 */
public class MPlayer implements IMPlayer,MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,MediaPlayer.OnVideoSizeChangedListener,
        MediaPlayer.OnPreparedListener,MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnErrorListener,SurfaceHolder.Callback{

    private MediaPlayer player;

    private String source;
    private IMDisplay display;
    private PlayerCallback callback;

    private boolean isVideoSizeMeasured=false;  //
    private boolean isMediaPrepared=false;      //
    private boolean isSurfaceCreated=false;     //
    private boolean isUserWantToPlay=false;     //
    private boolean isResumed=false;            //
	
    private boolean flag = false;				//标志是否图片视频轮播

    private boolean mIsCrop=false;

    private IMPlayListener mPlayListener;

    private int currentVideoWidth;              //
    private int currentVideoHeight;             //
    
    List<String> urlList = new ArrayList<String>();
    
    private int position;

    private void createPlayerIfNeed(){
        if(null==player){
            player=new MediaPlayer();
            player.setScreenOnWhilePlaying(true);
            player.setOnBufferingUpdateListener(this);
            player.setOnVideoSizeChangedListener(this);
            player.setOnCompletionListener(this);
            player.setOnPreparedListener(this);
            player.setOnSeekCompleteListener(this);
            player.setOnErrorListener(this);
        }
    }

    private void playStart(){
        if(isVideoSizeMeasured&&isMediaPrepared&&isSurfaceCreated&&isUserWantToPlay&&isResumed){
            player.setDisplay(display.getHolder());
            player.start();
            display.onStart(this);
            if(mPlayListener!=null){
                mPlayListener.onStart(this);
            }
        }
    }

    private void playPause(){
        if(player!=null&&player.isPlaying()){
            player.pause();
            display.onPause(this);
            if(mPlayListener!=null){
                mPlayListener.onPause(this);
            }
        }
    }

    private boolean checkPlay(){
        if(source==null|| source.length()==0){
            return false;
        }
        return true;
    }

    public void setPlayListener(IMPlayListener listener){
        this.mPlayListener=listener;
    }

    /**
     * @param isCrop 
     */
    public void setCrop(boolean isCrop){
        this.mIsCrop=isCrop;
        if(display!=null&&currentVideoWidth>0&&currentVideoHeight>0){
            tryResetSurfaceSize(display.getDisplayView(),currentVideoWidth,currentVideoHeight);
        }
    }

    public boolean isCrop(){
        return mIsCrop;
    }

    /**
     * 判断是否在播放
     * @return 
     */
    public boolean isPlaying(){
        return player!=null&&player.isPlaying();
    }

    //重写Surface大小
    private void tryResetSurfaceSize(final View view, int videoWidth, int videoHeight){
        ViewGroup parent= (ViewGroup) view.getParent();
        int width=parent.getWidth();
        int height=parent.getHeight();
        if(width>0&&height>0){
            final LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) view.getLayoutParams();
            if(mIsCrop){
                float scaleVideo=videoWidth/(float)videoHeight;
                float scaleSurface=width/(float)height;
                if(scaleVideo<scaleSurface){
                    params.width=width;
                    params.height=height;// (int) (width/scaleVideo);
                    params.setMargins(0,(height-params.height)/2,0,(height-params.height)/2);
                }else{
                    params.height=height;
                    params.width=width;// (int) (height*scaleVideo);
                    params.setMargins((width-params.width)/2,0,(width-params.width)/2,0);
                }
            }else{
                if(videoWidth>width||videoHeight>height){
                    float scaleVideo=videoWidth/(float)videoHeight;
                    float scaleSurface=width/height;
                    if(scaleVideo>scaleSurface){
                        params.width=width;
                        params.height=height;// (int) (width/scaleVideo);
                        params.setMargins(0,(height-params.height)/2,0,(height-params.height)/2);
                    }else{
                        params.height=height;
                        params.width=width;// (int) (height*scaleVideo);
                        params.setMargins((width-params.width)/2,0,(width-params.width)/2,0);
                    }
                }
            }
            view.setLayoutParams(params);
        }
    }
    
    public void setCallback(PlayerCallback callback){
    	this.callback = callback;
    }

    @Override
    public void setSource(ArrayList<String> urls,boolean flag) throws MPlayerException {
    	this.flag = flag;
    	this.urlList = urls;
    	position = 0;
        this.source=urlList.get(0);
        createPlayerIfNeed();
        isMediaPrepared=false;
        isVideoSizeMeasured=false;
        player.reset();
        try {
            player.setDataSource(urlList.get(position));
            player.prepareAsync();
        } catch (IOException e) {
            throw new MPlayerException("set source error",e);
        }
    }
    
    private void setSource(String url) throws MPlayerException {
        this.source=url;
        createPlayerIfNeed();
        isMediaPrepared=false;
        isVideoSizeMeasured=false;
        player.reset();
        try {
            player.setDataSource(url);
            player.prepareAsync();
        } catch (IOException e) {
            throw new MPlayerException("set source error",e);
        }
    }

    @Override
    public void setDisplay(IMDisplay display) {
        if(this.display!=null&&this.display.getHolder()!=null){
            this.display.getHolder().removeCallback(this);
        }
        this.display=display;
        this.display.getHolder().addCallback(this);
    }

    @Override
    public void play() throws MPlayerException {
        if(!checkPlay()){
            throw new MPlayerException("Please setSource");
        }
        createPlayerIfNeed();
        isUserWantToPlay=true;
        playStart();
    }

    @Override
    public void pause() {
        isUserWantToPlay=false;
        playPause();
    }

    @Override
    public void onPause() {
        isResumed=false;
        playPause();
    }

    @Override
    public void onResume() {
        isResumed=true;
        playStart();
    }

    @Override
    public void onDestroy() {
        if(player!=null){
            player.release();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d("Test","MPlayer onCompleteion function");
    	if (urlList.size()>0) {
    		if (position<(urlList.size()-1)) {
				position++;
			}else if(position == (urlList.size()-1)){
				if (flag) {
				    Log.d("Test", "Complete the video");
					callback.complete();
					return;
				}
				position = 0;
			}
			try {
				setSource(urlList.get(position));
			} catch (MPlayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			if (flag) {
				callback.complete();
				return;
			}
			player.start();
		}
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isMediaPrepared=true;
        Log.d("Test","Media Prepared");
        playStart();
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        Log.d("Test","视频原宽高->"+width+"/"+height);
        if(width>0&&height>0){
            this.currentVideoWidth=width;
            this.currentVideoHeight=height;
            tryResetSurfaceSize(display.getDisplayView(),width,height);
            isVideoSizeMeasured=true;
            playStart();
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d("Test", "onError = " + what);
        return false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(display!=null&&holder==display.getHolder()){
            isSurfaceCreated=true;
            if(player!=null){
                player.setDisplay(holder);
                player.seekTo(player.getCurrentPosition());
            }
            playStart();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(display!=null&&holder==display.getHolder()){
            isSurfaceCreated=false;
        }
    }
}
