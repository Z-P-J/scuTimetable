package com.scu.timetable.ui.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.scu.timetable.utils.TimetableHelper;
import com.scu.timetable.utils.TimetableWidgtHelper;
import com.scu.timetable.utils.content.SPHelper;

import java.util.Timer;
import java.util.TimerTask;

public class TimetableWidget extends AppWidgetProvider {

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d("TimetableWidget", "onEnabled");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, WidgetService.class));
        } else {
            context.startService(new Intent(context, WidgetService.class));
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            super.onReceive(context, intent);
            return;
        }

        String action = intent.getAction();
        if (action != null) {
            if ("android.appwidget.action.APPWIDGET_UPDATE".equals(action)) {
                if (intent.getBooleanExtra("visitor_mode", false)) {
                    TimetableHelper.startVisitorMode(context);
                    Toast.makeText(context, "游客模式！", Toast.LENGTH_SHORT).show();
                }
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                int [] appIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, TimetableWidget.class));
                appWidgetManager.updateAppWidget(appIds, TimetableWidgtHelper.refreshViews(context));
            }
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] iArr) {
        for (int i : iArr) {
            appWidgetManager.updateAppWidget(i, TimetableWidgtHelper.refreshViews(context));
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.d("TimetableWidget", "onDeleted");
        context.stopService(new Intent(context, WidgetService.class));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d("TimetableWidget", "onDisabled");
        context.stopService(new Intent(context, WidgetService.class));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int i, Bundle bundle) {
        appWidgetManager.updateAppWidget(i, TimetableWidgtHelper.refreshViews(context));
    }
}
