package io.kristal.snackbarplugin;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import org.cobaltians.cobalt.Cobalt;
import org.cobaltians.cobalt.fragments.CobaltFragment;
import org.cobaltians.cobalt.plugin.CobaltAbstractPlugin;
import org.cobaltians.cobalt.plugin.CobaltPluginWebContainer;
import org.json.JSONObject;

public class SnackbarPlugin extends CobaltAbstractPlugin
{
	public static final String TAG = SnackbarPlugin.class.getSimpleName();

	private static SnackbarPlugin instance;

	public static CobaltAbstractPlugin getInstance()
	{
		if (instance == null)
		{
			instance = new SnackbarPlugin();
		}
		return instance;
	}
	
	@Override
	public void onMessage(@NonNull final CobaltPluginWebContainer webContainer, @NonNull String action,
			@Nullable JSONObject data, @Nullable final String callbackChannel)
	{
		String text = data.optString("text", "Your snackbar text");
		int duration = data.optInt("duration", Snackbar.LENGTH_SHORT);

		Snackbar snackbar = Snackbar.make(webContainer.getFragment().getView(), text, duration);

		String button = data.optString("button");
		if (button != null && button.length() > 0) {
			snackbar.setAction(button, new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Cobalt.publishMessage(null, callbackChannel);
				}
			});

			// Set action color
			String colorCode = data.optString("buttonColor");
			if (colorCode != null) {
				try {
					int color = Cobalt.parseColor(colorCode);
					snackbar.setActionTextColor(color);
				}
				catch (IllegalArgumentException e) {
					Log.d(TAG, "Snackbars invalid buttonColor");
				}
			}
		}
		else if (duration == Snackbar.LENGTH_INDEFINITE) {
			Log.d(TAG, "Snackbars with INFINITE duration must have an action, using LONG instead");
			snackbar.setDuration(Snackbar.LENGTH_LONG);
		}

		snackbar.show();
	}
}
