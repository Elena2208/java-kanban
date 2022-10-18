package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.FileBackedTasksManager;
import manager.TaskManager;
import task.Subtask;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HandlerSubtask implements HttpHandler {
    public static final String PATH = "/tasks/task";
    public static final String HTTP_GET = "GET";
    public static final String HTTP_POST = "POST";
    public static final String HTTP_DELETE = "DELETE";
    private final TaskManager taskManager = new FileBackedTasksManager("task.csv");
    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        final String query = httpExchange.getRequestURI().getQuery();
        final String method = httpExchange.getRequestMethod();
        String body;
        String responseJson;
        int id;
        switch (method) {
            case HTTP_GET:
                if (query == null) {
                    final Map<Integer, Subtask> subtasks = taskManager.getListSubtask();
                    responseJson = gson.toJson(subtasks);
                    response(httpExchange, 200, responseJson);
                    return;
                }
                id = getId(httpExchange);
                Subtask subtask = taskManager.getSubtaskId(id).orElseThrow();
                responseJson = gson.toJson(subtask);
                response(httpExchange, 200, responseJson);
                return;
            case HTTP_POST:
                body = readText(httpExchange);
                if (body.isEmpty()) {
                    System.out.println("The request body is empty");
                    response(httpExchange, 400, "");
                    return;
                }
                id = getId(httpExchange);
                subtask = gson.fromJson(body, Subtask.class);
                if (id == 0) {
                    taskManager.createSubtask(subtask);
                    response(httpExchange, 201, "Подзадача успешно создана");

                } else {
                    taskManager.updateSubtask(id, subtask);
                    response(httpExchange, 204, "Подзадача успешно обновлена");

                }
                return;
            case HTTP_DELETE:
                if (query == null) {
                    taskManager.clearSubtask();
                    response(httpExchange, 204, "Подзадачи удалены");
                    return;
                }
                id = getId(httpExchange);
                taskManager.deleteSubtaskById(id);
                response(httpExchange, 204, "Подзадача удалена");
                return;
            default:
                System.out.println("Неизвестный запрос " + httpExchange.getRequestURI());
                response(httpExchange, 404, "");
        }

    }

    private void response(HttpExchange httpExchange, int statusCode, String text) throws IOException {

        httpExchange.sendResponseHeaders(statusCode, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(text.getBytes());
        }
    }

    private String readText(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private int getId(HttpExchange httpExchange) {
        final String query = httpExchange.getRequestURI().getQuery();
        String idParam = query.substring(3);
        return Integer.parseInt(idParam);
    }
}
