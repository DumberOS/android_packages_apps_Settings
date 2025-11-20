/*
 * Copyright (C) 2024
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.fuelgauge;

/** Utility class that exposes the hard-coded battery capacity override. */
public final class BatteryCapacityOverride {

    private static final int BATTERY_CAPACITY_MAH = 2_100;

    private BatteryCapacityOverride() {}

    /** Returns {@code true} when an override is active. */
    public static boolean isEnabled() {
        return BATTERY_CAPACITY_MAH > 0;
    }

    /** Returns the overridden nominal capacity in mAh. */
    public static int getCapacityMah() {
        return BATTERY_CAPACITY_MAH;
    }

    /** Returns the overridden nominal capacity in uAh. */
    public static int getCapacityUah() {
        return BATTERY_CAPACITY_MAH * 1_000;
    }

    /**
     * Returns the remaining capacity (in mAh) for the provided percentage when the override is
     * active.
     */
    public static int getChargeCounterMah(int batteryLevel) {
        final int clampedLevel = Math.min(Math.max(batteryLevel, 0), 100);
        return (BATTERY_CAPACITY_MAH * clampedLevel) / 100;
    }
}
