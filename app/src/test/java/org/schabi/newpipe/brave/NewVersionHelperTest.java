package org.schabi.newpipe.brave;

import android.os.Build;

import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertEquals;


public class NewVersionHelperTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    private static void setFinalStatic(final Field field, final Object newValue) throws Exception {
        field.setAccessible(true);

        final Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }

    @SuppressWarnings({"checkstyle:LineLength", "checkstyle:MethodName"})
    @Test
    public void checkJsonDataWith_alternative_apks() throws Exception {
        final String stdUrl = "stdUrl";
        final String expectedAltUrl = "altUrl";

        final String withAltJSON = "{\"flavors\":{\"github\":{\"stable\":{\"apk\":\"" + stdUrl + "\","
                + "\"alternative_apks\":[{\"alternative\":\"conscrypt\",\"url\":\"" + expectedAltUrl + "\"}],"
                + "\"version\":\"1\",\"version_code\":1}}}}";

        final JsonObject obj = JsonParser.object().from(withAltJSON).getObject("flavors")
                .getObject("github").getObject("stable");

        // 1. test simulating running on KitKat
        setFinalStatic(Build.VERSION.class.getField("SDK_INT"), 19);
        String result = NewVersionHelper.getAlternativeUrlOnKitkat(obj, stdUrl);
        assertEquals(expectedAltUrl, result);

        // 2. test simulating running > KitKat
        setFinalStatic(Build.VERSION.class.getField("SDK_INT"), 21);
        result = NewVersionHelper.getAlternativeUrlOnKitkat(obj, stdUrl);
        assertEquals(stdUrl, result);
    }

    @SuppressWarnings({"checkstyle:LineLength", "checkstyle:MethodName"})
    @Test
    public void checkJsonDataWithout_alternative_apks() throws Exception {
        final String stdUrl = "stdUrl";
        final String expectedStdUrl = stdUrl;

        final String withoutAltJSON = "{\"flavors\":{\"github\":{\"stable\":{\"apk\":\"" + stdUrl + "\","
                + "\"version\":\"1\",\"version_code\":1}}}}";

        final JsonObject obj = JsonParser.object().from(withoutAltJSON).getObject("flavors")
                .getObject("github").getObject("stable");


        // 1. test simulating running on KitKat
        setFinalStatic(Build.VERSION.class.getField("SDK_INT"), 19);
        String result = NewVersionHelper.getAlternativeUrlOnKitkat(obj, stdUrl);
        assertEquals(expectedStdUrl, result);

        // 2. test simulating running > KitKat
        setFinalStatic(Build.VERSION.class.getField("SDK_INT"), 21);
        result = NewVersionHelper.getAlternativeUrlOnKitkat(obj, stdUrl);
        assertEquals(stdUrl, result);
    }
}
