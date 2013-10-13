package com.arcusapp.soundbox.model;

public interface MediaPlayerServiceListener {
    public void onMediaPlayerStateChanged();
    public void onExceptionRaised(Exception ex);
}
