package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.FileBackedTasksManager;
import manager.TaskManager;
import task.Epic;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HandlerEpic implements HttpHandler {
    public static final String PATH = "/tasks/task";
    public static final String HTTP_GET ="GET";
    public static final String HTTP_POST ="POST";
    public static final String HTTP_DELETE ="DELETE";
    private final TaskManager taskManager=new FileBackedTasksManager("task.csv");
    private final Gson gson=new Gson();

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
                    final Map<Integer, Epic> epics = taskManager.getListEpic();
                    responseJson = gson.toJson(epics);
                    response(httpExchange, 200, responseJson);
                    return;
                }
                id = getId(httpExchange);
                Epic epic = taskManager.getEpicId(id).orElseThrow();
                responseJson = gson.toJson(epic);
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
                epic = gson.fromJson(body, Epic.class);
                if (id == 0) {
                    taskManager.createEpic(epic);
                    response(httpExchange, 201, "Подзадача успешно создана");

                } else {
                    taskManager.updateEpic(id, epic);
                    response(httpExchange, 204, "Подзадача успешно обновлена");

                }
                return;
            case HTTP_DELETE:
                if (query == null) {
                    taskManager.clearEpic();
                    response(httpExchange, 204, "Подзадачи удалены");
                    return;
                }
                id = getId(httpExchange);
                taskManager.deleteEpicById(id);
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
