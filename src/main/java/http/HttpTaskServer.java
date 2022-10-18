package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import handler.HandlerEpic;
import handler.HandlerSubtask;
import handler.HandlerTask;
import manager.TaskManager;
import task.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.NoSuchElementException;


public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer httpServer;
    private final Gson gson;
    private final TaskManager taskManager;
    public static final String HTTP_GET ="GET";

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = new Gson();
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks", this::handler);
        httpServer.createContext(HandlerTask.PATH, new HandlerTask());
        httpServer.createContext(HandlerSubtask.PATH, new HandlerSubtask());
        httpServer.createContext(HandlerEpic.PATH, new HandlerEpic());
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
        try {
            switch (path) {
                case "":
                    if (!method.equals(HTTP_GET)) {
                        System.out.println("ждет GET запрос, а получил: " + method);
                        response(httpExchange, 400, "");
                    }
                    responseJson = gson.toJson(taskManager.getPrioritizedTasks());
                    response(httpExchange, 200, responseJson); return;
                case "history":
                    if (!method.equals(HTTP_GET)) {
                        System.out.println("ждет "+HTTP_GET+" запрос, а получил: " + method);
                        response(httpExchange, 400, "");return;
                    }
                    responseJson = gson.toJson(taskManager.getHistory());
                    response(httpExchange, 200, responseJson);return;
                case "subtask/epic":
                    if (!method.equals(HTTP_GET)) {
                        System.out.println("ждет "+HTTP_GET+" запрос, а получил: "+ method);
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

    private void response(HttpExchange httpExchange, int statusCode, String text) throws IOException {

        httpExchange.sendResponseHeaders(statusCode, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(text.getBytes());
        }
    }

    private int getId(HttpExchange httpExchange) {
        final String query = httpExchange.getRequestURI().getQuery();
        String idParam = query.substring(3);
        return Integer.parseInt(idParam);
    }
}
