/*
 * Created by zhangxiangwei on 2020/09/09.
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

package com.sensorsdata.abtest.core;


import android.app.Application;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.View;

import com.sensorsdata.abtest.entity.Experiment;
import com.sensorsdata.abtest.util.AlarmManagerUtils;
import com.sensorsdata.abtest.util.SPUtils;
import com.sensorsdata.abtest.util.TaskRunner;
import com.sensorsdata.analytics.android.sdk.SALog;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.analytics.android.sdk.listener.SAJSListener;

import java.lang.ref.WeakReference;
import java.util.Map;

public class SensorsABTestHelper implements SAJSListener, AppStateManager.AppStateChangedListener {

    private Context mContext;
    private CountDownTimer mCountDownTimer;

    public void init(Context context) {
        this.mContext = context;
        SPUtils.getInstance().init(context);
        SensorsABTestCacheManager.getInstance().loadExperimentsFromDiskCache();
        SensorsDataAPI.sharedInstance().addSAJSListener(this);
        requestExperimentsAndUpdateCacheWithRetry();
        if (context instanceof Application) {
            Application application = (Application) context;
            AppStateManager appStateManager = new AppStateManager();
            appStateManager.addAppStateChangedListener(this);
            application.registerActivityLifecycleCallbacks(appStateManager);
        }
    }

    /**
     * 处理从 JS 发送过来的请求
     *
     * @param view WebView
     * @param content 消息实体
     */
    @Override
    public void onReceiveJSMessage(WeakReference<View> view, String content) {
        new SensorsABTestH5Helper(view, content).handlerJSMessage();
    }

    @Override
    public void onEnterForeground(boolean resumeFromBackground) {
        SALog.i("AppStartupManager", "onEnterForeground");
        AlarmManagerUtils.getInstance(mContext).setUpAlarm();
    }

    @Override
    public void onEnterBackground() {
        SALog.i("AppStartupManager", "onEnterBackground");
        AlarmManagerUtils.getInstance(mContext).cancelAlarm();
    }

    private void requestExperimentsAndUpdateCacheWithRetry() {
        TaskRunner.getBackHandler().post(new Runnable() {
            @Override
            public void run() {
                cancelTimer();
                if (mCountDownTimer == null) {
                    mCountDownTimer = new CountDownTimer(120 * 1000, 30 * 1000) {
                        @Override
                        public void onTick(long l) {
                            new SensorsABTestApiRequestHelper<>().requestExperimentsAndUpdateCache(new IApiCallback<Map<String, Experiment>>() {
                                @Override
                                public void onSuccess(Map<String, Experiment> stringExperimentMap) {
                                    cancelTimer();
                                }

                                @Override
                                public void onFailure(int errorCode, String message) {

                                }
                            });
                        }

                        @Override
                        public void onFinish() {
                        }
                    };
                }
                mCountDownTimer.start();
            }
        });
    }

    private void cancelTimer() {
        try {
            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
            }
        } catch (Exception e) {
            SALog.printStackTrace(e);
        } finally {
            mCountDownTimer = null;
        }
    }
}
