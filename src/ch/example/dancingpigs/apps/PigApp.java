package ch.example.dancingpigs.apps;

import ch.example.dancingpigs.MainService;

public class PigApp extends OverlayApp {

	public PigApp(MainService service, String _packageName, String _className) {
		super(service, _packageName, _className);
		activities = new PigActivity[]{new PigActivity(service, this, _packageName, _className)};
	}

}
