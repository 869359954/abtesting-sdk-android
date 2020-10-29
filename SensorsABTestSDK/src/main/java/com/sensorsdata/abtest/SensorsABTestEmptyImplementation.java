/*
 * Created by zhangxiangwei on 2020/10/28.
 * Copyright 2015－2020 Sensors Data Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sensorsdata.abtest;

import android.content.Context;

public class SensorsABTestEmptyImplementation extends SensorsABTest {

    SensorsABTestEmptyImplementation() {
    }

    @Override
    public SensorsABTestConfigOptions getConfigOptions() {
        return null;
    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public <T> T fetchCacheABTest(String experimentId, T defaultValue) {
        return null;
    }

    @Override
    public <T> void asyncFetchABTest(String experimentId, T defaultValue, OnABTestReceivedData<T> callBack) {
    }

    @Override
    public <T> void asyncFetchABTest(String experimentId, T defaultValue, int timeoutMillSeconds, OnABTestReceivedData<T> callBack) {
    }

    @Override
    public <T> void fastFetchABTest(String experimentId, T defaultValue, OnABTestReceivedData<T> callBack) {
    }

    @Override
    public <T> void fastFetchABTest(String experimentId, T defaultValue, int timeoutMillSeconds, OnABTestReceivedData<T> callBack) {
    }
}
