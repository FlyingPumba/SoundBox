/*
 * SoundBox - Android Music Player
 * Copyright (C) 2013 Iván Arcuschin Moreno
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

package com.arcusapp.soundbox.util;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FontUtils {

    public static interface FontTypes {
        public static String THIN = "Thin";
        public static String THIN_ITALIC = "Thin_Italic";
    }

    /**
     * map of font types to font paths in assets
     */
    private static Map fontMap = new HashMap();
    static {
        fontMap.put(FontTypes.THIN, "fonts/Roboto-Thin.ttf");
        fontMap.put(FontTypes.THIN_ITALIC, "fonts/Roboto-ThinItalic.ttf");
    }

    /* cache for loaded Roboto typefaces*/
    private static Map typefaceCache = new HashMap();

    /**
     * Creates Roboto typeface and puts it into cache
     * @param context
     * @param fontType
     * @return
     */
    private static Typeface getRobotoTypeface(Context context, String fontType) {
        String fontPath = (String)fontMap.get(fontType);
        if (!typefaceCache.containsKey(fontType))
        {
            typefaceCache.put(fontType, Typeface.createFromAsset(context.getAssets(), fontPath));
        }
        return (Typeface)typefaceCache.get(fontType);
    }

    /**
     * Gets roboto typeface according to passed typeface style settings.
     * Will get Roboto-Bold for Typeface.BOLD etc
     * @param context
     * @param originalTypeface
     * @return
     */
    private static Typeface getRobotoTypeface(Context context, Typeface originalTypeface) {
        String robotoFontType = FontTypes.THIN; //default Thin Roboto font
        if (originalTypeface != null) {
            int style = originalTypeface.getStyle();
            switch (style) {
                case Typeface.ITALIC:
                    robotoFontType = FontTypes.THIN_ITALIC;
            }
        }
        return getRobotoTypeface(context, robotoFontType);
    }

    /**
     * Walks ViewGroups, finds TextViews and applies Typefaces taking styling in consideration
     * @param context - to reach assets
     * @param view - root view to apply typeface to
     */
    public static void setRobotoFont(Context context, View view)
    {
        if (view instanceof ViewGroup)
        {
            for (int i = 0; i < ((ViewGroup)view).getChildCount(); i++)
            {
                setRobotoFont(context, ((ViewGroup)view).getChildAt(i));
            }
        }
        else if (view instanceof TextView)
        {
            Typeface currentTypeface = ((TextView) view).getTypeface();
            ((TextView) view).setTypeface(getRobotoTypeface(context, currentTypeface));
        }
    }
}
