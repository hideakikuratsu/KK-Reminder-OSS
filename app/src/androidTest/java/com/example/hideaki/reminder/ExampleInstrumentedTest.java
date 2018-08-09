package com.example.hideaki.reminder;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented repeat_custom_week_picker, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
  @Test
  public void useAppContext() {
    // Context of the app under repeat_custom_week_picker.
    Context appContext = InstrumentationRegistry.getTargetContext();

    assertEquals("com.example.hideaki.reminder", appContext.getPackageName());
  }
}
