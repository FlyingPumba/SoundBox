package com.arcusapp.soundbox.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.adapter.FoldersActivityListAdapter;

public class FoldersActivity extends Activity implements View.OnClickListener {

	private TextView txtDir;
	private ListView myListView;
	private FoldersActivityListAdapter myAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folders);

		txtDir = (TextView) findViewById(R.id.txtDir);
		myAdapter = new FoldersActivityListAdapter(this, txtDir);

		myListView = (ListView) findViewById(R.id.foldersActivityList);
		myListView.setAdapter(myAdapter);
		myListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				myAdapter.onItemClick(position);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.folders, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		myAdapter.backPressed();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnLogo) {
			finish();
		} else if (v.getId() == R.id.btnPlayFolder) {
			// TODO: play current folder.
		}
	}
}
