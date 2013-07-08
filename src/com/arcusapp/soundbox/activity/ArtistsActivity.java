package com.arcusapp.soundbox.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.adapter.ArtistsActivityAdapter;

public class ArtistsActivity extends Activity implements View.OnClickListener {

	private ExpandableListView myExpandableList;
	private ArtistsActivityAdapter myAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_artists);

		myAdapter = new ArtistsActivityAdapter(this);

		myExpandableList = (ExpandableListView) findViewById(R.id.expandableListArtists);
		// le quito la flechita para abajo en los grupos/artistas
		// myExpandableList.setGroupIndicator(null);

		myExpandableList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
					myAdapter.onArtistLongClick(position);
					return true;
				} else if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
					int groupPosition = ExpandableListView.getPackedPositionGroup(id);
					int childPosition = ExpandableListView.getPackedPositionChild(id);
					myAdapter.onAlbumLongClick(groupPosition, childPosition);
					return true;
				}
				return false;
			}
		});

		myExpandableList.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				myAdapter.onAlbumClick(groupPosition, childPosition);
				return false;
			}
		});

		myExpandableList.setAdapter(myAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.artists, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnLogo3) {
			finish();
		}
	}
}
