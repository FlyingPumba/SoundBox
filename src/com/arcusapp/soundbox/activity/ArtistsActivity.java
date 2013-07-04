package com.arcusapp.soundbox.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.arcusapp.soundbox.MediaProvider;
import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.model.BundleExtra;

public class ArtistsActivity extends Activity implements View.OnClickListener {

	// TODO: set the OnClick method on the layout xml
	private Button btnLogo3;
	private ExpandableListView expList;
	// XXX: make a proper adapter and do not use this variables.
	private String displayTabArtist = "  ";
	private String displayTabAlbum = "      ";

	private MediaProvider media;
	private Intent playActivityIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_artists);

		media = new MediaProvider();
		playActivityIntent = new Intent(this, PlayActivity.class);

		btnLogo3 = (Button) findViewById(R.id.btnLogo3);
		btnLogo3.setOnClickListener(this);

		expList = (ExpandableListView) findViewById(R.id.expandableListArtists);
		expList.setGroupIndicator(null);// le quito la flechita para abajo en
										// los grupos/artistas

		expList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {

					TextView vi = (TextView) view;

					Bundle b = new Bundle();
					List<String> ids = media.getSongsFromArtist(vi.getText().toString().replaceFirst(displayTabArtist, ""));
					b.putStringArrayList(BundleExtra.SONGS_ID_LIST, new ArrayList<String>(ids));
					b.putString(BundleExtra.CURRENT_ID, ids.get(0));
					playActivityIntent.putExtras(b);

					startActivity(playActivityIntent);

					return true;
				}

				return false;
			}
		});

		expList.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				TextView vi = (TextView) v;
				Bundle b = new Bundle();

				List<String> ids = media.getSongsFromAlbum(vi.getText().toString().replaceFirst(displayTabAlbum, ""));
				b.putStringArrayList(BundleExtra.SONGS_ID_LIST, new ArrayList<String>(ids));
				b.putString(BundleExtra.CURRENT_ID, ids.get(0));

				playActivityIntent.putExtras(b);
				startActivity(playActivityIntent);

				return false;
			}

		});

		// agrego este listener para que solo haya un grupo abierto a la vez
		/*
		 * expList.setOnGroupExpandListener(new OnGroupExpandListener(){ int
		 * previousGroup = -1;
		 * 
		 * @Override public void onGroupExpand(int groupPosition) {
		 * 
		 * if(groupPosition != previousGroup) {
		 * expList.collapseGroup(previousGroup); } previousGroup =
		 * groupPosition; }
		 * 
		 * });
		 */
		expList.setAdapter(new MyExpandableListAdapter(this));
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

	public class MyExpandableListAdapter extends BaseExpandableListAdapter {

		private List<String> mArtists;
		private List<List<String>> mAlbums;

		public MyExpandableListAdapter(Context context) {
			// get all the Artists
			mArtists = media.getAllArtists();

			// get the Albums foreach Artist
			mAlbums = new ArrayList<List<String>>();
			for (int i = 0; i < mArtists.size(); i++) {
				mAlbums.add(media.getAlbumsFromArtist(mArtists.get(i).toString()));
			}
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			// in this method you must set the text to see the parent/group on the list

			TextView textView = new TextView(getApplicationContext());
			textView.setText(displayTabArtist + getGroup(groupPosition).toString());
			textView.setTextSize(25);
			textView.setTextColor(Color.BLACK);
			if (isExpanded) {
				textView.setBackgroundColor(Color.YELLOW);
			} else {
				textView.setBackgroundColor(Color.WHITE);
			}

			return textView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			// in this method you must set the text to see the children on the list
			TextView textView = new TextView(getApplicationContext());
			textView.setText(displayTabAlbum + getChild(groupPosition, childPosition).toString());
			textView.setTextSize(20);
			textView.setTextColor(Color.BLACK);
			return textView;
		}

		@Override
		public Object getGroup(int groupPosition) {
			// gets the title of each parent/group
			return mArtists.get(groupPosition).toString();
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// gets the name of each item
			return mAlbums.get(groupPosition).get(childPosition).toString();
		}

		@Override
		public int getGroupCount() {
			// counts the number of group/parent items so the list knows how many
			// times calls getGroupView() method
			return mArtists.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// counts the number of children items so the list knows how many times
			// calls getChildView() method
			return mAlbums.get(groupPosition).size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}
}
