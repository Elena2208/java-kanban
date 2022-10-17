import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import http.HttpTaskServer;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {

    private HttpTaskServer server;
    private Subtask subtaskSecond;
    private Subtask subtaskFirst;
    private Task task;
    private Epic epic;
    private Gson gson;

    @BeforeEach
    void beforeEach() throws IOException {

        TaskManager manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        task = new Task("New_task", "Test", Status.NEW);
        epic = new Epic("New_epic", "test");
        subtaskFirst = new Subtask("SubtaskFirst", "Test", Status.NEW, epic.getIdTask(),
                50, LocalDateTime.of(0, 1, 25, 10, 10, 10));
        subtaskSecond = new Subtask("SubtaskSecond", "Test", Status.IN_PROGRESS, epic.getIdTask(),
                100, LocalDateTime.of(0, 1, 10, 18, 25, 36));
        manager.getTaskId(task.getIdTask());
        manager.getEpicId(epic.getIdTask());
        manager.getSubtaskId(subtaskFirst.getIdTask());
        manager.getSubtaskId(subtaskSecond.getIdTask());
        server.start();
        gson=new Gson();
    }

    @AfterEach
    void serverStop() {
        server.stop();
    }

    @Test
    void getTasksTest() throws IOException, InterruptedException {

        var client = HttpClient.newHttpClient();
        var url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse <String > response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        var type = new TypeToken<Map<Integer, Task>>() {
        }.getType();
        Map<Integer, Task> tasks =gson.fromJson(response.body(),type);
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
    }

    @Test
    void getEpicsTest() throws IOException, InterruptedException {

        var client = HttpClient.newHttpClient();
        var url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        var type = new TypeToken<Map<Integer, Epic>>() {
        }.getType();
        Map<Integer, Epic> epics = gson.fromJson(response.body(), type);
        assertNotNull(epics);
        assertEquals(1, epics.size());
    }

    @Test
    void getSubtasksTest() throws IOException, InterruptedException {

        var client = HttpClient.newHttpClient();
        var url = URI.create("http://localhost:8080/tasks/subtask");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response  = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        var type = new TypeToken<Map<Integer, Subtask>>() {  }.getType();
        Map<Integer, Subtask> subtasks = gson.fromJson(response.body(), type);
        assertNotNull(subtasks);
        assertEquals(2, subtasks.size());

    }

    @Test
    void getTaskTest() throws IOException, InterruptedException {

        var client = HttpClient.newHttpClient();
        var url = URI.create("http://localhost:8080/tasks/task/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        var type = new TypeToken<Task>() {
        }.getType();
        Task task1 = gson.fromJson(response.body(), type);
        assertNotNull(task1);
        assertEquals(task, task1);
    }

    @Test
    void getHistoryTest() throws IOException, InterruptedException {
        var client = HttpClient.newHttpClient();
        var url = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        var type = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasks = gson.fromJson(response.body(), type);
        assertNotNull(tasks);
        assertEquals(4, tasks.size());

    }

    @Test
    void getPrioritizedTasksTest() throws IOException, InterruptedException {

        var client = HttpClient.newHttpClient();
        var url = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        var type = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasks = gson.fromJson(response.body(), type);
        assertNotNull(tasks);
        assertEquals(3, tasks.size());

    }

    @Test
    void createTaskTest() throws IOException, InterruptedException {

        var client = HttpClient.newHttpClient();
        var url = URI.create("http://localhost:8080/tasks/task");
        var json = gson.toJson(task);
        var body = HttpRequest.BodyPublishers.ofString(json);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void createEpicTest() throws IOException, InterruptedException {

        var client = HttpClient.newHttpClient();
        var url = URI.create("http://localhost:8080/tasks/epic");
        var json = gson.toJson(epic);
        var body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void createSubtaskTest() throws IOException, InterruptedException {

        var client = HttpClient.newHttpClient();
        var url = URI.create("http://localhost:8080/tasks/subtask");
        var json = gson.toJson(subtaskFirst);
        var body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void deleteTaskTest() throws IOException, InterruptedException {

        var client = HttpClient.newHttpClient();
        var url = URI.create("http://localhost:8080/tasks/task/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

    }

    @Test
    void deleteTasksTest() throws IOException, InterruptedException {

        var client = HttpClient.newHttpClient();
        var url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        var type = new TypeToken<Map<Integer, Task>>() {        }.getType();
        Map<Integer, Task> tasks = gson.fromJson(response.body(), type);
        assertNull(tasks);

    }

}