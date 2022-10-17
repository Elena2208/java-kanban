package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.TaskManager;
import task.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.NoSuchElementException;


public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer httpServer;
    private Gson gson;
    private final TaskManager taskManager;


    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = new Gson();
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks", this::handler);

    }

    public void start() {
        System.out.println("Server start on port " + PORT);
        httpServer.start();
    }

    public void stop() {
        System.out.println("Server stopped ");
        httpServer.stop(0);

    }

    private void handler(HttpExchange httpExchange) throws IOException {
        final String path = httpExchange.getRequestURI().getPath().substring(7);
        final String query = httpExchange.getRequestURI().getQuery();
        final String method = httpExchange.getRequestMethod();
        String responseJson;
        int id;
        gson = new Gson();
        try {
            switch (path) {
                case "":
                    if (!method.equals("GET")) {
                        System.out.println("ждет GET запрос, а получил: " + method);
                        response(httpExchange, 400, "");
                    }
                    responseJson = gson.toJson(taskManager.getPrioritizedTasks());
                    response(httpExchange, 200, responseJson); return;
                case "history":
                    if (!method.equals("GET")) {
                        System.out.println("ждет GET запрос, а получил: " + method);
                        response(httpExchange, 400, "");return;
                    }
                    responseJson = gson.toJson(taskManager.getHistory());
                    response(httpExchange, 200, responseJson);return;
                case "task":
                    handlerTask(httpExchange);return;
                case "subtask":
                    handlerSubtask(httpExchange);return;
                case "epic":
                    handlerEpic(httpExchange);return;
                case "subtask/epic":
                    if (!method.equals("GET")) {
                        System.out.println("ждет GET запрос, а получил: " + method);
                        response(httpExchange, 400, "");
                        return;
                    }
                    id = getId(httpExchange);
                    final Map<Integer, Subtask> mapSubEpic = taskManager.getListSubtaskEpic(id);
                    responseJson = gson.toJson(mapSubEpic);
                    response(httpExchange, 200, responseJson);return;
                default:
                    System.out.println("Неизвестный запрос " + httpExchange.getRequestURI());
                    response(httpExchange, 404, "");

            }
        } catch (NoSuchElementException e) {
            response(httpExchange, 404, "");
        } catch (IllegalArgumentException e) {
            response(httpExchange, 400, "");
        } catch (Error error) {
            response(httpExchange, 500, "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            httpExchange.close();
        }
    }

    private void handlerTask(HttpExchange httpExchange) throws IOException {
        final String query = httpExchange.getRequestURI().getQuery();
        final String method = httpExchange.getRequestMethod();
        String body;
        String responseJson;
        int id;
        switch (method) {
            case "GET":
                if (query == null) {
                    final Map<Integer, Task> tasks = taskManager.getListTask();
                    responseJson = gson.toJson(tasks);
                    response(httpExchange, 200, responseJson);
                    return;
                }
                id = getId(httpExchange);
                Task task = taskManager.getTaskId(id).orElseThrow();
                responseJson = gson.toJson(task);
                response(httpExchange, 200, responseJson);
                return;
            case "POST":
                body = readText(httpExchange);
                if (body.isEmpty() || body.isBlank()) {
                    System.out.println("The request body is empty");
                    response(httpExchange, 400, "");
                    return;
                }
                id = getId(httpExchange);
                task = gson.fromJson(body, Task.class);
                if (id == 0) {
                    taskManager.createTask(task);
                    response(httpExchange, 201, "Задача успешно создана");

                } else {
                    taskManager.updateTask(id, task);
                    response(httpExchange, 204, "Задача успешно обновлена");

                }
                return;
            case "DELETE":
                if (query == null) {
                    taskManager.clearTask();
                    response(httpExchange, 204, "задачи удалены");
                    return;
                }
                id = getId(httpExchange);
                taskManager.deleteTaskById(id);
                response(httpExchange, 204, "Задача удалена");
                return;
            default:
                System.out.println("Неизвестный запрос " + httpExchange.getRequestURI());
                response(httpExchange, 404, "");
        }

    }

    private void handlerSubtask(HttpExchange httpExchange) throws IOException {
        final String query = httpExchange.getRequestURI().getQuery();
        final String method = httpExchange.getRequestMethod();
        String body;
        String responseJson;
        int id;
        switch (method) {
            case "GET":
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
            case "POST":
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
            case "DELETE":
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

    private void handlerEpic(HttpExchange httpExchange) throws IOException {
        final String query = httpExchange.getRequestURI().getQuery();
        final String method = httpExchange.getRequestMethod();
        String body;
        String responseJson;
        int id;
        switch (method) {
            case "GET":
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
            case "POST":
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
            case "DELETE":
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
