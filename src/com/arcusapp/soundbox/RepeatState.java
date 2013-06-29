package com.arcusapp.soundbox;

/**
 * Enum with the 3 differents ways in which the MediaPlayer can handle repetition.
 * The repetetition refers to the whole list, and not to the individual tracks.
 * Unless RandomState is set to Party, the MediaPlayer will play all the songs in the list. Otherwise, this state won't matter.
 */
public enum RepeatState {
	/**
	 * The MediaPlayer is going to play all the songs in the songslist, and stop after playing the last song.
	 * Even if we navigate the songslist, forward or backward, the MediaPlayer will only stop if it finishes playing the last song.
	 * If we press Prev in the first song, the MediaPlayer will start playing the last song of the list. The RepeatState will not change.
	 * If we press Next in the last song, the MediaPlayer will show the first song of the list, but it will not play it. The RepeatState will not change.
	 */
	Off,
	/**
	 * The MediaPlayer is going to play ALL the songs in the songslist, and when it finishes the last song it will begin the list again.
	 * Even if we navigate the songslist, forward or backward, the MediaPlayer will only begin the list again if it finishes playing the last song.
	 * If we press Prev in the first song, the MediaPlayer will start playing the last song of the list. The RepeatState will not change.
	 * If we press Next in the last song, the MediaPlayer will start playing the first song of the list. The RepeatState will not change.
	 */
	All,
	/**
	 * The MediaPlayer is going to play ONE song over and over, until it is paused or stopped.
	 * Navigating the songslist, forward or backard, changes the song to repeat.
	 */
	One
}