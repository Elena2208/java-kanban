import http.HttpTaskManager;
import http.KVServer;
import manager.FileBackedTasksManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTasksManagerTest extends TaskManagerTest<HttpTaskManager> {

    protected KVServer server;

    @BeforeEach
    public void beforeEach() throws IOException {
        server = new KVServer();
        server.start();
        manager = new HttpTaskManager(KVServer.PORT);
        task = new Task("New_task", "Test", Status.NEW);
        epic = new Epic("New_epic", "test");
        subtaskFirst = new Subtask("SubtaskFirst", "Test", Status.NEW, epic.getIdTask(), 50,
                LocalDateTime.of(0, 1, 25, 10, 10, 10));
        subtaskSecond = new Subtask("SubtaskSecond", "Test", Status.IN_PROGRESS, epic.getIdTask(), 100,
                LocalDateTime.of(0, 1, 10, 18, 25, 36));
    }


    @AfterEach
    void serverStop() {
        server.stop();
    }

    @Test
    void loadFromServerTest() {

        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubtask(subtaskFirst);
        manager.createSubtask(subtaskSecond);
        manager.getTaskId(task.getIdTask());
        manager.getEpicId(epic.getIdTask());
        manager.getSubtaskId(subtaskFirst.getIdTask());
        manager.getSubtaskId(subtaskSecond.getIdEpic());

        manager.load();

        Map<Integer, Task> tasks = manager.getListTask();
        Map<Integer, Subtask> subtasks = manager.getListSubtask();
        Map<Integer, Epic> epics = manager.getListEpic();
        Set <Task> treeSet=manager.getPrioritizedTasks();
        List<Task> history=manager.getHistory();
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertNotNull(subtasks);
        assertEquals(2, subtasks.size());
        assertNotNull(epic);
        assertEquals(1, epics.size());


    }

}