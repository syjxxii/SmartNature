package edu.berkeley.cs160.smartnature;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import java.util.ArrayList;

public class GardenScreen extends Activity implements OnTouchListener, OnClickListener {
	
	final int ZOOM_DURATION = 3000;
	final int NEW_GARDEN = 0;
	final int SHARE_GARDEN = 1;
	ArrayList<Plot> plots;
	AlertDialog dialog;
	ZoomControls zoom;
	Handler mHandler;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new Handler();
		plots = new ArrayList<Plot>();
		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey("name")) {
			setTitle(extras.getString("name"));
			initMockData();
		} else
			showDialog(NEW_GARDEN);
		setContentView(R.layout.garden);
		findViewById(R.id.garden_layout).setOnTouchListener(this);
		zoom = (ZoomControls) findViewById(R.id.zoom_controls);
		zoom.setVisibility(View.GONE);
		zoom.setOnZoomInClickListener(this);
		zoom.setOnZoomOutClickListener(this);
		
		boolean hintsOn = getSharedPreferences("global", Context.MODE_PRIVATE).getBoolean("show_hints", true);
		String hint = "Click on Add Plot in the menu to add a plot\n" +
		 	"Press on a plot to view its plants/info";
		if (hintsOn) {
			((TextView)findViewById(R.id.garden_hint)).setText(hint);
			((TextView)findViewById(R.id.garden_hint)).setVisibility(View.VISIBLE);
			}
	}
	
	public void initMockData() {
		ShapeDrawable s1 = new ShapeDrawable(new RectShape());
		s1.setBounds(20, 60, 80, 200);
		ShapeDrawable s2 = new ShapeDrawable(new OvalShape());
		s2.setBounds(140, 120, 190, 190);
		plots.add(new Plot(s1, "Jerry's Plot"));
		plots.add(new Plot(s2, "Amy's Plot"));
	}

	@Override
	public Dialog onCreateDialog(int id) {
		switch (id) {
			case SHARE_GARDEN: return shareGarden();
			default: return newGarden();	
		}
	}
	public Dialog newGarden() {
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.text_entry_dialog, null);
		DialogInterface.OnClickListener confirmed = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				/*Intent intent = new Intent(GardenScreen.this, EditPlot.class);
				Bundle bundle = new Bundle();
				bundle.putString("name", gardenName);
				intent.putExtras(bundle);
				startActivity(intent);*/
				String gardenName = ((EditText) textEntryView.findViewById(R.id.dialog_text_entry)).getText().toString();
				setTitle(gardenName);
			}
		};
		DialogInterface.OnClickListener canceled = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				finish();
			}
		};
		dialog = new AlertDialog.Builder(this)
			.setTitle(R.string.new_garden_prompt)
			.setView(textEntryView)
			.setPositiveButton(R.string.alert_dialog_ok, confirmed)
			.setNegativeButton(R.string.alert_dialog_cancel, canceled)
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
	public Dialog shareGarden() {
		LayoutInflater factory = LayoutInflater.from(this);
		final View shareGardenView = factory.inflate(R.layout.share_garden_dialog, null);
		DialogInterface.OnClickListener confirmed = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				Toast.makeText(GardenScreen.this, "Garden has been uploaded to the Internet", Toast.LENGTH_SHORT).show();
			}
		};
		
		EditText passwordInput = (EditText) shareGardenView.findViewById(R.id.garden_password);
		passwordInput.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				if (((EditText)view).getText().length() > 0)
					shareGardenView.findViewById(R.id.garden_permissions2).setEnabled(true);
				else
					shareGardenView.findViewById(R.id.garden_permissions2).setEnabled(false);
				return false;
			}
		});
		return new AlertDialog.Builder(this)
			//.setTitle(R.string.new_garden_prompt)
			//.setInverseBackgroundForced(true)
			.setView(shareGardenView)
			.setPositiveButton(R.string.alert_dialog_share, confirmed)
			.setNegativeButton(R.string.alert_dialog_cancel, null)
			.create();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.garden_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.m_home:
				finish();
				break;
			case R.id.m_share:
				showDialog(SHARE_GARDEN);
				break;
	}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		handleZoom();
		return false;
	}
	
	@Override
	public void onClick(View view) {
		handleZoom();
		
	}
	
	public void handleZoom() {
		mHandler.removeCallbacks(autoHide);
		if (!zoom.isShown())
			zoom.show(); //zoom.setVisibility(View.VISIBLE);
		mHandler.postDelayed(autoHide, ZOOM_DURATION);
	}
	Runnable autoHide = new Runnable() {	
		@Override
		public void run() {
			if (zoom.isShown()) {
				mHandler.removeCallbacks(autoHide);			
				zoom.hide(); //zoom.setVisibility(View.GONE);
			}
		}
	};

}