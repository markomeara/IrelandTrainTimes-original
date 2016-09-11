package ie.markomeara.irelandtraintimes;

/**
 * Injects the dagger dependencies
 */
public class Injector {

	private static AppComponent sComponent;

	public static void init() {
		if (sComponent == null) {
			sComponent = DaggerAppComponent.builder().appModule(new AppModule()).build();
		}
	}

	public static AppComponent get() {
		return sComponent;
	}

	static void setComponent(AppComponent component) {
		sComponent = component;
	}

	static void setModule(AppModule module) {
		sComponent = DaggerAppComponent.builder().appModule(module).build();
	}

}
