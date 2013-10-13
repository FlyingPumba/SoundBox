package com.arcusapp.soundbox.model;

public interface MediaPlayerServiceListener {
    public void onSongCompletion();
    public void onExceptionRaised(Exception ex);
}
