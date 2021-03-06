package dk.gruppe7.common;

import dk.gruppe7.common.audio.AudioPlayer;
import dk.gruppe7.common.resources.ResourceManager;

/**
 *
 * @author Holst & Harald
 */
public class GameData {
    private int screenWidth;
    private int screenHeight;
    private long tickCount = 0;
    private float deltaTime;
    private ResourceManager resourceManager;
    private AudioPlayer audioPlayer;

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public float getDeltaTime() {
        return deltaTime;
    }

    public void setDeltaTime(float deltaTime) {
        this.deltaTime = deltaTime;
    }
    
    public void incrementTickCount() {
        this.tickCount++;
    }
    
    public long getTickCount() {
        return this.tickCount;
    }
    
    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public void setResourceManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    public void setAudioPlayer(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }
    
}