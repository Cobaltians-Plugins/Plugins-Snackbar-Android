package io.kristal.snackbarplugin;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.cobaltians.cobalt.Cobalt;
import org.cobaltians.cobalt.fragments.CobaltFragment;
import org.cobaltians.cobalt.plugin.CobaltAbstractPlugin;
import org.cobaltians.cobalt.plugin.CobaltPluginWebContainer;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SnackbarPlugin extends CobaltAbstractPlugin
{
	public static final String TAG = SnackbarPlugin.class.getSimpleName();

	private static SnackbarPlugin instance;
	private String mPluginName;

	public static CobaltAbstractPlugin getInstance(CobaltPluginWebContainer webContainer) {
		if (instance == null) {
			instance = new SnackbarPlugin();
		}

		instance.addWebContainer(webContainer);

		return instance;
	}

	private SnackbarPlugin() {

	}

	@Override
	public void onMessage(final CobaltPluginWebContainer webContainer, final JSONObject message) {
		try {
			JSONObject data = message.getJSONObject(Cobalt.kJSData);
			mPluginName = message.getString(Cobalt.kJSPluginName);

			String text = data.optString("text", "Your snackbar text");
			int duration = data.optInt("duration", Snackbar.LENGTH_SHORT);

			Snackbar snackbar = Snackbar.make(webContainer.getFragment().getView(), text, duration);

			String button = data.optString("button");
			if (button != null && button.length() > 0) {
				snackbar.setAction(button, new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						CobaltFragment fragment = webContainer.getFragment();
						if (fragment != null) {
							fragment.sendPlugin(mPluginName, null);
						}
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
		catch (JSONException e) {

		}
	}
}
