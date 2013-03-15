package net.kevxu.sense;

import java.util.Formatter;

import net.kevxu.senselib.Sense;
import net.kevxu.senselib.SensorNotAvailableException;
import net.kevxu.senselib.StepDetector;
import net.kevxu.senselib.StepDetector.StepListener;
import android.app.Activity;
import android.hardware.Sensor;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity implements StepListener {

	private TextView mTitle;
	private TextView mContent;

	private Sense mSense;
	private StepDetector mStepDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTitle = (TextView) findViewById(R.id.activity_main_title_textview);
		mContent = (TextView) findViewById(R.id.activity_main_content_textview);

		try {
			mSense = new Sense(this);
			mStepDetector = new StepDetector(this, this);

			mTitle.setText(R.string.accel_in_gravity_direction);
		} catch (SensorNotAvailableException e) {
			String sensor;
			switch (e.getSensorType()) {
			case Sensor.TYPE_LINEAR_ACCELERATION:
				sensor = getString(R.string.sensor_linear_acceleration);
				break;
			case Sensor.TYPE_GRAVITY:
				sensor = getString(R.string.sensor_gravity);
				break;
			default:
				sensor = getString(R.string.sensor_not_available);
				break;
			}
			Formatter formatter = new Formatter();
			formatter.format(getString(R.string.sensor_not_available), sensor);
			mTitle.setText(formatter.toString());
			formatter.close();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		mSense.close();
		mStepDetector.close();
	}

	@Override
	protected void onResume() {
		super.onResume();

		mSense.reload();
		mStepDetector.reload();
	}

	@Override
	public void onStep() {

	}

	@Override
	public void onValue(final float value) {
		synchronized (this) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mContent.setText(String.valueOf(value));
				}
			});
		}
	}

}
