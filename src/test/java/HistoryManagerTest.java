import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {
    private InMemoryTaskManager manager;

    @BeforeEach
    void beforeEach() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void addTaskTest() {
        manager.createSubtask(new Subtask());
        manager.createEpic(new Epic());
        assertTrue(manager.getHistory().isEmpty());

    }

    @Test
    void getHistoryTest() {
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void addTaskDuplicateTest() {
        Subtask subtask = new Subtask();
        Task task = new Task();
        manager.createSubtask(subtask);
        manager.createTask(task);
        manager.getTaskId(task.getIdTask());
        manager.getSubtaskId(subtask.getIdTask());
        manager.getTaskId(task.getIdTask());
        manager.getTaskId(task.getIdTask());
        int counter = 0;
        for (Task task1 : manager.getHistory()) {
            if (task1.equals(task)) counter++;
        }
        assertEquals(1, counter);

    }

    @Test
    void getHistoryDuplicateTest() {
        Subtask subtask = new Subtask();
        Task task = new Task();
        manager.createSubtask(subtask);
        manager.createTask(task);
        manager.getTaskId(task.getIdTask());
        manager.getSubtaskId(subtask.getIdTask());
        manager.getTaskId(task.getIdTask());
        manager.getTaskId(task.getIdTask());
        assertEquals(2, manager.getHistory().size());
    }

    @Test
    void deleteTaskIsHistoryTest() {

        Task taskFirst = new Task();
        Task taskSecond = new Task();
        Task taskThird = new Task();
        Task taskFourth = new Task();
        manager.getTaskId(taskFirst.getIdTask());
        manager.getTaskId(taskSecond.getIdTask());
        manager.getTaskId(taskThird.getIdTask());
        manager.getTaskId(taskFourth.getIdTask());
        manager.deleteTaskById(taskFirst.getIdTask());
        assertFalse(manager.getHistory().contains(taskFirst));
        manager.deleteTaskById(taskFourth.getIdTask());
        assertFalse(manager.getHistory().contains(taskFourth));
    }

}



