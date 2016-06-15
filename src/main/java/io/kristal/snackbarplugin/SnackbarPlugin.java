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

			String text = data.optString("text", "Your snackbar text");
			int duration = data.optInt("duration", Snackbar.LENGTH_SHORT);

			Snackbar snackbar = Snackbar.make(webContainer.getFragment().getView(), text, duration);

			JSONObject actionOptions = data.optJSONObject("action");
			if (actionOptions != null) {
				// Set action callback
				final String callback = message.optString(Cobalt.kJSCallback);
				snackbar.setAction(actionOptions.optString("text", "Your action"), new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if (callback != null) {
							webContainer.getFragment().sendCallback(callback, null);
						} else {
							if (Cobalt.DEBUG) {
								Log.d(TAG, "No callback specified");
							}
						}
					}
				});

				// Set action color
				int actionColor = parseColorCode(actionOptions.optString("color"));
				if (actionColor != Integer.MAX_VALUE) {
					snackbar.setActionTextColor(actionColor);
				}
			}
			else if (duration == Snackbar.LENGTH_INDEFINITE) {
				Log.d(TAG, "Snackbars with INFINITE duration must have an action, using LONG instead");
				snackbar.setDuration(Snackbar.LENGTH_SHORT);
			}

			snackbar.show();
		}
		catch (JSONException e) {

		}
	}

	/**
	 * Parse color code in RGB format
	 * @param code String in #XXXXXX format
	 * @return parsed int value or Integer.MAX_VALUE if input is null or does not match the required format
	 */
	private static int parseColorCode(String code) {
		if (code != null) {
			// RGB color
			Matcher rgbMatcher = Pattern.compile("#([0-9ABCDEFabcdef]{6})").matcher(code);
			if (rgbMatcher.matches()) {
				return (int) Long.parseLong(rgbMatcher.group(1).toLowerCase(), 16) + 0xFF000000;
			}
		}

		return Integer.MAX_VALUE;
	}
}
