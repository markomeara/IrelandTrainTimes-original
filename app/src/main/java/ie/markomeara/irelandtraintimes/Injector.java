package ie.markomeara.irelandtraintimes;

import dagger.ObjectGraph;

/**
 * Injects the dagger dependencies
 */
public class Injector {

	private static ObjectGraph sObjectGraph = ObjectGraph.create(new AppModule(IrelandTrainTimesApplication.getInstance()));

	public static <T> void inject(T obj) {
		sObjectGraph.inject(obj);
	}

}
