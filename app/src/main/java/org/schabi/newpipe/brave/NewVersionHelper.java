package org.schabi.newpipe.brave;

import android.os.Build;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;

public final class NewVersionHelper {
    private NewVersionHelper() { }

    public static String checkForAlternativeApkUrl(final JsonObject githubStableObject,
                                                   final String apkLocationUrl) {

        if (githubStableObject.has("alternative_apks")) {
            final JsonArray alternativeUrls = githubStableObject.getArray("alternative_apks");
            for (final Object object: alternativeUrls) {
                final JsonObject alternativeUrlObject = (JsonObject) object;

                if (alternativeUrlObject.has("alternative")
                        && "conscrypt".equals(alternativeUrlObject.get("alternative"))) {
                    return alternativeUrlObject.getString("url");
                }
            }
        }
        return apkLocationUrl;
    }

    private static boolean isKitKat() {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT;
    }

    /**
     * check if an alternative Url is given for KitKat users. If not return
     * apkLocationUrl.
     *
     * @param githubStableObject
     * @param apkLocationUrl
     * @return returns url that points to the best release for this device
     */
    public static String getAlternativeUrlOnKitkat(final JsonObject githubStableObject,
                                                   final String apkLocationUrl) {
        if (isKitKat()) {
            return checkForAlternativeApkUrl(githubStableObject, apkLocationUrl);
        } else {
            return apkLocationUrl;
        }
    }
}
