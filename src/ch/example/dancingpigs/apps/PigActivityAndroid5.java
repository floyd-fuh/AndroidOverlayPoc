package ch.example.dancingpigs.apps;

import ch.example.dancingpigs.MainService;

public class PigActivityAndroid5 extends PigActivity {

	public PigActivityAndroid5(MainService service, OverlayApp app,
			String _appName, String _className) {
		super(service, app, _appName, _className);
		this.className = OverlayActivity.ANDROID_LOLLIPOP_UNKNOWN_ACTIVITY_CLASS;
	}
}
