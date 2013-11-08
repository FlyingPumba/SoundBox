/*
 * SoundBox - Android Music Player
 * Copyright (C) 2013  Iván Arcuschin Moreno
 *
 * This file is part of SoundBox.
 *
 * SoundBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * SoundBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SoundBox.  If not, see <http://www.gnu.org/licenses/>.
 */

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
