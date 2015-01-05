/*
 * <!--
 *    Copyright (C) 2015 The NamelessROM Project
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * -->
 */
package com.android.launcher3.nameless;

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.ActivityOptions;
import android.app.IActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.RemoteException;
import android.os.UserHandle;
import android.widget.Toast;

import com.android.launcher3.R;

import java.util.List;

public class Utils {
    private static final String SYSTEMUI_PACKAGE = "com.android.systemui";

    private static Toast sToast;

    public static void switchToLastApp(final Context context) throws RemoteException {
        final ActivityManager.RecentTaskInfo lastTask = getLastTask(context, UserHandle.myUserId());
        if (lastTask == null || lastTask.id < 0) {
            if (sToast != null) {
                sToast.cancel();
            }
            final String msg = context.getString(R.string.no_last_app);
            sToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            sToast.show();
            return;
        }

        final IActivityManager am = ActivityManagerNative.getDefault();
        final ActivityOptions opts = ActivityOptions.makeCustomAnimation(context,
                com.android.internal.R.anim.last_app_in,
                com.android.internal.R.anim.last_app_out);

        am.moveTaskToFront(lastTask.id, ActivityManager.MOVE_TASK_NO_USER_ACTION, opts.toBundle());
    }

    private static ActivityManager.RecentTaskInfo getLastTask(Context context, int userId)
            throws RemoteException {
        final String defaultHomePackage = resolveCurrentLauncherPackage(context, userId);
        final IActivityManager am = ActivityManagerNative.getDefault();
        final List<ActivityManager.RecentTaskInfo> tasks = am.getRecentTasks(5,
                ActivityManager.RECENT_IGNORE_UNAVAILABLE, userId);

        for (int i = 1; i < tasks.size(); i++) {
            ActivityManager.RecentTaskInfo task = tasks.get(i);
            if (task.origActivity != null) {
                task.baseIntent.setComponent(task.origActivity);
            }
            String packageName = task.baseIntent.getComponent().getPackageName();
            if (!packageName.equals(defaultHomePackage)
                    && !packageName.equals(SYSTEMUI_PACKAGE)) {
                return tasks.get(i);
            }
        }

        return null;
    }

    private static String resolveCurrentLauncherPackage(Context context, int userId) {
        final Intent launcherIntent = new Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_HOME);
        final PackageManager pm = context.getPackageManager();
        final ResolveInfo launcherInfo = pm.resolveActivityAsUser(launcherIntent, 0, userId);
        return launcherInfo.activityInfo.packageName;
    }

}
