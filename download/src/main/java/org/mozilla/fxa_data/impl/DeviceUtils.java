/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fxa_data.impl;

import android.content.Context;
import org.mozilla.gecko.R;

import java.util.concurrent.atomic.AtomicBoolean;

/** A collection of methods for manipulating the device. */
public class DeviceUtils {
    private DeviceUtils() {}

    private static AtomicBoolean isInit = new AtomicBoolean(false);
    private static boolean isTablet;

    // synchronized so init is called only once (i.e. doesn't have to be idempotent).
    public static synchronized void init(final Context context) {
        if (isInit.get()) { return; }

        isTablet = context.getResources().getBoolean(R.bool.is_xlarge);

        isInit.set(true);
    }

    private static void assertIsInit() {
        if (!isInit.get()) { throw new IllegalStateException("Expected DeviceUtils to be initialized before use"); }
    }

    public static boolean isTablet() { assertIsInit(); return isTablet; }
}
