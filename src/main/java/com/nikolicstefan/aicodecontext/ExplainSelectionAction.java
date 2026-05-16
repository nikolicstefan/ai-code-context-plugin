package com.nikolicstefan.aicodecontext;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ExplainSelectionAction extends AnAction {

    private static final String AI_URL = "http://localhost:11434/api/generate";
    private static final String AI_MODEL = "llama3.2:1b";

    private void showNotification(Project project, String message, NotificationType type) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("AiCodeContextNotifications")
                .createNotification(message, type)
                .notify(project);
    }

    private String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }

    private String extractResponse(String json) {
        String marker = "\"response\":\"";
        int start = json.indexOf(marker);

        if (start < 0) {
            return json;
        }

        start += marker.length();
        int end = json.indexOf("\",", start);

        if (end < 0) {
            return json;
        }

        return json.substring(start, end)
                .replace("\\n", "\n")
                .replace("\\\"", "\"");
    }

    private String askAi(String prompt) throws IOException, InterruptedException {
        String requestBody = """
                {
                    "model": "%s",
                    "prompt": "%s",
                    "stream": false
                }
                """.formatted(AI_MODEL, escapeJson(prompt));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AI_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new IOException("HTTP " + response.statusCode() + ": " + response.body());
        }

        return extractResponse(response.body());
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

        VirtualFile virtualFile = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE);
        String fileName = virtualFile != null ? virtualFile.getName() : "unknown file";

        showNotification(
                project,
                "Sending request to a local AI model...",
                NotificationType.INFORMATION
        );

        new Thread(() -> {
            try {
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

                String response = askAi(prompt);

                ApplicationManager.getApplication().invokeLater(() ->
                        Messages.showInfoMessage(
                                project,
                                response,
                                "AI Code Explanation"
                        )
                );
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                ApplicationManager.getApplication().invokeLater(() ->
                        Messages.showErrorDialog(
                                project,
                                e.getMessage(),
                                "AI Request Failed"
                        )
                );
            } catch (IOException e) {
                ApplicationManager.getApplication().invokeLater(() ->
                        Messages.showErrorDialog(
                                project,
                                e.getMessage(),
                                "AI Request Failed"
                        )
                );
            }
        }).start();
    }
}
