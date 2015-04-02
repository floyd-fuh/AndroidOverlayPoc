package ch.example.dancingpigs.apps;

import ch.example.dancingpigs.MainService;

public class OverlayActivityAndroid5 extends OverlayActivity {

	public OverlayActivityAndroid5(MainService service, OverlayApp app,
			String _appName, String _className) {
		super(service, app, _appName, _className);
		this.className = OverlayActivity.ANDROID_LOLLIPOP_UNKNOWN_ACTIVITY_CLASS;
	}
}
