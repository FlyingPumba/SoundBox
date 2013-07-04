package com.arcusapp.soundbox.model;

/**
 * Enum with the 3 differents ways in which the MediaPlayer can handle randomness.
 * The RandomState refers to the order in which the individual tracks of a songslist are played.
 * In other words, this changes the behavior of the Next and Previous actions.
 */
public enum RandomState {
	/**
	 * The list is shuffled.
	 */
	Shuffled,
	/**
	 * The list is alphabetical.
	 */
	Off,
	/**
	 * The list is Random. This seems a little confusing, but it isn't.
	 * On this state, we don't have a list order for playing the songs but we store the immediate songs, prev and next, to the actual song.
	 * 
	 * We allways have something like this:
	 * PrevSong --- ActualSong --- NextSong
	 */
	Random
}
