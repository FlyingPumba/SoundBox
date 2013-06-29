package com.arcusapp.soundbox;

/**
 * Enum with the 3 differents ways in which the MediaPlayer can handle repetition.
 * The repetetition refers to the whole list, and not to the individual tracks.
 */
public enum RepeatState {
	/**
	 * The MediaPlayer is going to play all the songs in the songslist, and stop after playing the last song.
	 */
	Off,
	/**
	 * The MediaPlayer is going to play ALL the songs in the songslist, and when it finishes the last song it will begin the list again.
	 */
	All,
	/**
	 * The MediaPlayer is going to play ONE song over and over, until it is paused or stopped.
	 */
	One
}