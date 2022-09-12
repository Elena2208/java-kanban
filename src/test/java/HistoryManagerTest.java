import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

        Task taskFirst = new Task("taskFirst ","", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2022,9,11,9,00,00));
        Task taskSecond = new Task("taskSecond","", Status.NEW, Duration.ofMinutes(20), LocalDateTime.of(2022,9,11,10,00,00));
        Task taskThird = new Task("taskThird","", Status.NEW, Duration.ofMinutes(30), LocalDateTime.of(2022,9,11,11,00,00));
        Task taskFourth = new Task("taskFourth","", Status.NEW, Duration.ofMinutes(100), LocalDateTime.of(2022,9,11,12,00,00));
        manager.createTask(taskFirst );
        manager.createTask(taskSecond);
        manager.createTask(taskThird );
        manager.createTask(taskFourth);
        List<Task> list=new ArrayList<>();
        manager.getTaskId(taskFirst.getIdTask());
        list.add(taskFirst);
        manager.getTaskId(taskSecond.getIdTask());
        list.add(taskSecond);
        manager.getTaskId(taskThird.getIdTask());
        list.add(taskThird);
        manager.getTaskId(taskFourth.getIdTask());
        list.add(taskFourth);
        manager.deleteTaskById(taskFirst.getIdTask());
        list.remove(taskFirst);
        assertFalse(manager.getHistory().contains(taskFirst));
        assertTrue(list.equals(manager.getHistory()));
        manager.deleteTaskById(taskFourth.getIdTask());
        list.remove(taskFourth);
        assertFalse(manager.getHistory().contains(taskFourth));
        assertTrue(list.equals(manager.getHistory()));
    }

}



