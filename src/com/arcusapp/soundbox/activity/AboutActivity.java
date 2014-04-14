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

package com.arcusapp.soundbox.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.webkit.WebView;
import android.widget.Toast;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.SoundBoxApplication;

public class AboutActivity extends PreferenceActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add the preferences
        addPreferencesFromResource(R.xml.about);

        showOpenSourceLicenses();
        seeOnGitHub();

        try{
            final PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            findPreference("version").setSummary(packageInfo.versionName + " " +packageInfo.versionCode);
        } catch (Exception ex) {
            Toast.makeText(this, getString(R.string.ErrorFetchingVersion), Toast.LENGTH_LONG);
            findPreference("version").setSummary("?");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SoundBoxApplication.notifyForegroundStateChanged(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SoundBoxApplication.notifyForegroundStateChanged(false);
    }

    private void showOpenSourceLicenses() {
        final Preference mOpenSourceLicenses = findPreference("open_source");
        mOpenSourceLicenses.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(final Preference preference) {
                final WebView webView = new WebView(AboutActivity.this);
                webView.loadUrl("file:///android_asset/license.html");
                AlertDialog licenseDialog = new AlertDialog.Builder(AboutActivity.this)
                        .setTitle(R.string.LabelAboutLicense)
                        .setView(webView)
                        .setPositiveButton(android.R.string.ok, null)
                        .create();
                licenseDialog.show();
                return true;
            }
        });
    }

    private void seeOnGitHub() {
        final Preference mGitHub = findPreference("github");
        mGitHub.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(final Preference preference) {
                Uri uri = Uri.parse("http://github.com/FlyingPumba/SoundBox");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;
            }
        });
    }
}