package com.arcusapp.soundbox;

/**
 * Enum with the 3 differents ways in which the MediaPlayer can handle randomness.
 * The RandomState refers to the order in which the individual tracks of a songslist are played.
 * In other words, this changes the behavior of the Next and Previous actions.
 */
public enum RandomState {
	/**
	 * The list is shuffled.
	 */
	On,
	/**
	 * The list is alphabetical.
	 */
	Off,
	/**
	 * The list is Random. This seems a little confusing, but it isn't.
	 * On this state, we don't have a list order for playing the songs but we store the immediate songs, prev and next, the actual song.
	 * Time line example:
	 * 1. The user picks up a song, and the next song is calculated by random and stored in the NextSong variable.
	 * 2. When the MediaPlayer finishes playing the song or the user press the Next button, the stored song (NextSong) is played and another song is calculated to take it's place.
	 * Also, the song that just finished being played is stored to the PrevSong variable. And so goes on.
	 * 3. When the user press the Previous button, the actual song is stored in NextSong, the Prevsong is played, and a new PrevSong is calculated.
	 * 
	 * In graphical, we allways have something like this:
	 * PrevSong --- ActualSong --- NextSong
	 * (The only one who can be null is PrevSong, but just the first time the list is played)
	 */
	Party
}
