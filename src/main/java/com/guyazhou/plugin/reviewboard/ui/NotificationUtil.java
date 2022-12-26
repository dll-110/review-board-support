package com.guyazhou.plugin.reviewboard.ui;

import com.intellij.notification.*;
import com.intellij.openapi.project.Project;

/**
 * @author YaZhou.Gu 2018/7/25
 */
public class NotificationUtil {

    private static NotificationGroup NOTIFICATIONS = NotificationGroupManager.getInstance()
            .getNotificationGroup("review-board-support");

    public static void notifyInformationNotifaction(String title, String content, Project project) {
        notifyNotification(title, content, NotificationType.INFORMATION, project);
    }

    public static void notifyWarningNotification(String title, String content, Project project) {
        notifyNotification(title, content, NotificationType.WARNING, project);
    }

    public static void notifyErrorNotification(String title, String content, Project project) {
        notifyNotification(title, content, NotificationType.ERROR, project);
    }

    private static void notifyNotification(String title, String content, NotificationType notificationType, Project project) {

        NotificationGroupManager.getInstance()
                .getNotificationGroup("review-board-support")
                .createNotification(content, notificationType)
                .notify(project);

    }

}
