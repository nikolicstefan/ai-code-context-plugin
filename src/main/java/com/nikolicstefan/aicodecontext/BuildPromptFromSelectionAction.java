package com.nikolicstefan.aicodecontext;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;

public class BuildPromptFromSelectionAction extends AnAction {

    private void showNotification(Project project, String message, NotificationType type) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("AiCodeContextNotifications")
                .createNotification(message, type)
                .notify(project);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);

        if (project == null || editor == null) {
            return;
        }

        String selectedText = editor.getSelectionModel().getSelectedText();

        if (selectedText == null || selectedText.isBlank()) {
            showNotification(
                    project,
                    "No code selected.",
                    NotificationType.WARNING
            );
            return;
        }

        String fileName = "unknown file";
        VirtualFile virtualFile = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE);

        if (virtualFile != null) {
            fileName = virtualFile.getName();
        }

        String prompt = """
                Explain the following code from file `%s`.
                
                Focus on:
                - purpose
                - important implementation details
                - possible issues or improvements
                
                ```text
                %s
                ```
                """.formatted(fileName, selectedText);

        CopyPasteManager.getInstance().setContents(new StringSelection(prompt));

        showNotification(
                project,
                "AI prompt copied to clipboard.",
                NotificationType.INFORMATION
        );
    }
}
