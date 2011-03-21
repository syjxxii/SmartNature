package edu.berkeley.cs160.smartnature;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class StartScreen extends ListActivity implements OnClickListener, OnItemClickListener {
	
	GardenAdapter adapter;
	ArrayList<Garden> gardens;
	AlertDialog dialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initMockData();
		
		getListView().setOnItemClickListener(this);
		((Button) findViewById(R.id.new_garden)).setOnClickListener(this);
	}
	
	public void initMockData() {
		gardens = new ArrayList<Garden>();
		gardens.add(new Garden(R.drawable.preview, "Berkeley Youth Alternatives"));
		gardens.add(new Garden(R.drawable.preview2, "Karl Linn"));
		gardens.add(new Garden(R.drawable.preview3, "Peralta"));
		adapter = new GardenAdapter(this, R.layout.list_item, gardens);
		setListAdapter(adapter);
	}

	@Override
	public Dialog onCreateDialog(int id) {
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.text_entry_dialog, null);

		DialogInterface.OnClickListener confirmed = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				/*
				SharedPreferences globalPrefs = getSharedPreferences("global", Context.MODE_PRIVATE);
				Editor e = globalPrefs.edit();
				e.putBoolean("sf", true);
				*/
				Intent intent = new Intent(StartScreen.this, GardenScreen.class);
				EditText input = (EditText) textEntryView.findViewById(R.id.dialog_text_entry);
				String gardenName = input.getText().toString();
				Bundle bundle = new Bundle();
				bundle.putString("name", gardenName);
				intent.putExtras(bundle);
				startActivity(intent);
				input.setText("");
			}
		};
		
		dialog = new AlertDialog.Builder(this)
			.setTitle(R.string.new_garden_prompt)
			.setView(textEntryView)
			.setPositiveButton(R.string.alert_dialog_ok, confirmed)
			.setNegativeButton(R.string.alert_dialog_cancel, null)
			.create();
		
		EditText input = (EditText) textEntryView.findViewById(R.id.dialog_text_entry);
		input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		        if (hasFocus)
		            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		    }
		});

		return dialog;
	}

	@Override
	public void onClick(View view) {
		//showDialog(0);
		startActivity(new Intent(this, GardenScreen.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}
	

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this, GardenScreen.class);
		Bundle bundle = new Bundle();
		bundle.putString("name", ((TextView)view.findViewById(R.id.garden_name)).getText().toString());
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.m_contact:
			case R.id.m_global_options:
				startActivity(new Intent(this, GlobalSettings.class));
				break;
			case R.id.m_help:
		}
		return super.onOptionsItemSelected(item);
	}
	
	class GardenAdapter extends ArrayAdapter<Garden> {
		private ArrayList<Garden> items;
		private LayoutInflater li;
		
		public GardenAdapter(Context context, int textViewResourceId, ArrayList<Garden> items) {
			super(context, textViewResourceId, items);
			li = ((ListActivity) context).getLayoutInflater();
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null)
				v = li.inflate(R.layout.list_item, null);
			Garden g = items.get(position);
			((TextView) v.findViewById(R.id.garden_name)).setText(g.getName());
			((ImageView) v.findViewById(R.id.preview_img)).setImageResource(g.getPreviewId());
			
			return v;
		}
	}
}
