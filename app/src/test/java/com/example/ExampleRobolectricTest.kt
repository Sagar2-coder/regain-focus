package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.FocusMode
import com.example.viewmodel.FocusViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Regain", appName)
  }

  @Test
  fun `verify focus mode calculations and strict lock defaults`() {
    val vm = FocusViewModel()
    assertEquals(FocusMode.POMODORO_25_5, vm.focusMode.value)
    assertEquals(25 * 60, vm.totalSeconds.value)
    assertFalse(vm.strictSettings.value.isStrictModeEnabled)

    vm.setFocusMode(FocusMode.DEEP_WORK_50_10)
    assertEquals(50 * 60, vm.totalSeconds.value)

    vm.toggleStrictMode(true)
    assertTrue(vm.strictSettings.value.isStrictModeEnabled)
  }
}
