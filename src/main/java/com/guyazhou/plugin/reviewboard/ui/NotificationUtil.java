package com.guyazhou.plugin.reviewboard.ui;

import com.intellij.notification.*;
import com.intellij.openapi.project.Project;

/**
 * @author YaZhou.Gu 2018/7/25
 */
public class NotificationUtil {


    public static void notifyInfomationNotifaction(String title, String content, Project project) {
        notifyNotification(title, content, NotificationType.INFORMATION, project);
    }

    public static void notifyWarningNotification(String title, String content, Project project) {
        notifyNotification(title, content, NotificationType.WARNING, project);
    }

    public static void notifyErrorNotification(String title, String content, Project project) {
        notifyNotification(title, content, NotificationType.ERROR, project);
    }

    private static void notifyNotification(String title, String content, NotificationType notificationType, Project project) {
        Notifications.Bus.notifyAndHide(new Notification(title,content,NotificationType.INFORMATION));
    }

}
