package ie.markomeara.irelandtraintimes.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import ie.markomeara.irelandtraintimes.R;

/**
 * Created by Mark on 21/10/2014.
 */
public class PropertiesUtils {

    private Context context;
    private static final String SECRET_PROPERTIES = "secrets.properties";


    public static String getSecretProperty(Context context, String propName) {

        String propVal = null;
        try {

            AssetManager assetManager = context.getAssets();

            InputStream propsFileContents = assetManager.open(SECRET_PROPERTIES);

            Properties properties = new Properties();
            properties.load(propsFileContents);
            propVal = properties.getProperty(propName);

        } catch (IOException e) {
            Log.e("PropertiesUtils", e.toString());
        }

        return propVal;

    }
}
