import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected static TaskManager manager = new InMemoryTaskManager();
    protected static Task task;
    protected static Subtask subtaskFirst;
    protected static Subtask subtaskSecond;
    protected static Epic epic;
    Random random = new Random();

    @Test
    void clearTaskTest() {
        manager.createTask(task);
        manager.clearTask();
        assertTrue(manager.getListTask().isEmpty());
    }

    @Test
    void clearTaskByListIsEmptyTest() {
        manager.clearTask();
        assertTrue(manager.getListTask().isEmpty());
    }

    @Test
    void clearSubtaskTest() {
        manager.createEpic(epic);
        manager.createSubtask(subtaskFirst);
        manager.createSubtask(subtaskSecond);
        manager.clearSubtask();
        assertTrue(manager.getListSubtask().isEmpty());
    }


    @Test
    void clearSubtaskByListIsEmptyTest() {
        manager.clearSubtask();
        assertTrue(manager.getListSubtask().isEmpty());
    }


    @Test
    void clearEpicTest() {
        manager.createEpic(epic);
        manager.clearEpic();
        assertTrue(manager.getListEpic().isEmpty());
        assertTrue(manager.getListSubtask().isEmpty());
    }


    @Test
    void clearEpicByListIsEmptyTest() {
        manager.clearEpic();
        assertTrue(manager.getListEpic().isEmpty());
        assertTrue(manager.getListSubtask().isEmpty());
    }


    @Test
    void createEpicTest() {
        int epicId = epic.getIdTask();
        manager.createEpic(epic);
        final Epic epicNew = manager.getEpicId(epicId).get();
        assertNotNull(epicNew, "Эпик не найден");
        assertEquals(epic, epicNew, "Эпики не совпадают");
    }

    @Test
    void createTaskTest() {
        int taskId = task.getIdTask();
        manager.createTask(task);
        final Task taskNew = manager.getTaskId(taskId).get();
        assertNotNull(taskNew, "Задача не найдена");
        assertEquals(task, taskNew, "Задачи  не совпадают");
    }

    @Test
    void createSubtaskTest() {
        int subtaskId = subtaskSecond.getIdTask();
        manager.createSubtask(subtaskSecond);
        final Subtask subtaskNew = manager.getSubtaskId(subtaskId).get();
        assertNotNull(subtaskNew, "Подзадача не найдена");
        assertEquals(subtaskSecond, subtaskNew, "Подзадачи не совпадают");
    }


    @Test
    void updateTaskTest() {
        int idTask = task.getIdTask();
        manager.createTask(task);
        Task taskNew = new Task("New_task", "Test", Status.IN_PROGRESS);
        manager.updateTask(idTask, taskNew);
        assertEquals(manager.getTaskId(idTask).get(), taskNew);
    }

    @Test
    void updateTaskTestByNotTaskTest() {
        int idTask = task.getIdTask();
        Task taskNew = new Task("New_task", "Test", Status.IN_PROGRESS);
        manager.updateTask(idTask, taskNew);
        assertFalse(manager.getTaskId(idTask).isPresent());
    }


    @Test
    void updateTaskTestByWrongIdTest() {
        int idTask = random.nextInt(1000);
        Task taskNew = new Task("New_task", "Test", Status.IN_PROGRESS);
        taskNew.setIdTask(idTask);
        manager.updateTask(idTask, taskNew);
        assertFalse(manager.getTaskId(idTask).isPresent());
    }


    @Test
    void updateEpicTest() {
        int idEpic = epic.getIdTask();
        manager.createEpic(epic);
        Epic epicNew = new Epic("EpicNew", "Test");
        manager.updateEpic(idEpic, epicNew);
        assertEquals(manager.getEpicId(idEpic).get(), epicNew);

    }

    @Test
    void updateEpicTestByNotEpicTest() {
        int idEpic = epic.getIdTask();
        Epic epicNew = new Epic("EpicNew", "Test");
        manager.updateEpic(idEpic, epicNew);
        assertFalse(manager.getEpicId(idEpic).isPresent());
    }

    @Test
    void updateEpicTestByWrongIdTest() {
        int idEpic = random.nextInt(1000);
        Epic epicNew = new Epic("EpicNew", "Test");
        epicNew.setIdTask(idEpic);
        manager.updateEpic(idEpic, epicNew);
        assertFalse(manager.getEpicId(idEpic).isPresent());
    }


    @Test
    void updateSubtaskTest() {
        int idSubtask = subtaskSecond.getIdTask();
        manager.createSubtask(subtaskSecond);
        manager.updateSubtask(idSubtask, subtaskFirst);
        assertEquals(manager.getSubtaskId(idSubtask).get(), subtaskFirst);
    }

    @Test
    void updateSubtaskByNoSubtaskTest() {
        int idSubtask = subtaskSecond.getIdTask();
        manager.updateSubtask(idSubtask, subtaskFirst);
        assertFalse(manager.getSubtaskId(idSubtask).isPresent());

    }

    @Test
    void updateSubtaskByWrongIdTest() {
        int idSubtask = random.nextInt(1000);
        subtaskFirst.setIdTask(idSubtask);
        manager.updateSubtask(idSubtask, subtaskFirst);
        assertFalse(manager.getSubtaskId(idSubtask).isPresent());
    }

    @Test
    void deleteTaskByIdTest() {
        final int idTask = task.getIdTask();
        manager.createTask(task);
        manager.deleteTaskById(idTask);
        assertFalse(manager.getTaskId(idTask).isPresent());
    }

    @Test
    void deleteTaskByIdListIsEmptyTest() {
        final int idTask = task.getIdTask();
        manager.deleteTaskById(idTask);
        assertFalse(manager.getTaskId(idTask).isPresent());
    }

    @Test
    void deleteTaskByIdByWrongIdTest() {
        final int idTask = random.nextInt(2300);
        manager.createTask(task);
        int sizeList = manager.getListTask().size();
        manager.deleteTaskById(idTask);
        assertTrue(manager.getListTask().size() == sizeList);
    }

    @Test
    void deleteSubtaskByIdTest() {
        final int idSubtask = subtaskFirst.getIdTask();
        int idEpic = subtaskFirst.getIdEpic();
        manager.createSubtask(subtaskFirst);
        manager.deleteSubtaskById(idSubtask);
        assertFalse(manager.getSubtaskId(idSubtask).isPresent());
        assertFalse(manager.getListSubtaskEpic(idEpic).containsValue(subtaskFirst));
    }

    @Test
    void deleteSubtaskByIdListIsEmptyTest() {
        final int idSubtask = subtaskFirst.getIdTask();
        manager.deleteSubtaskById(idSubtask);
        assertFalse(manager.getListSubtask().containsValue(subtaskFirst));
    }


    @Test
    void deleteSubtaskByIdWrongIdTest() {
        final int idSubtask = random.nextInt(2300);
        manager.createSubtask(subtaskFirst);
        int size = manager.getListSubtask().size();
        manager.deleteSubtaskById(idSubtask);
        assertTrue(manager.getListSubtask().size() == size);
    }


    @Test
    void deleteEpicByIdTest() {
        manager.createEpic(epic);
        final int idEpic = epic.getIdTask();
        manager.createSubtask(subtaskFirst);
        manager.deleteEpicById(idEpic);
        assertFalse(manager.getEpicId(idEpic).isPresent());

    }

    @Test
    void deleteEpicByIdListIsEmptyTest() {
        final int idEpic = epic.getIdTask();
        manager.deleteEpicById(idEpic);
        assertFalse(manager.getEpicId(idEpic).isPresent());
    }


    @Test
    void deleteEpicByIdWrongIdTest() {
        final int idEpic = random.nextInt(2300);
        manager.createEpic(epic);
        int size = manager.getListEpic().size();
        manager.deleteEpicById(idEpic);
        assertTrue(manager.getListEpic().size() == size);

    }

    @Test
    void getTaskIdTest() {
        final int idTask = task.getIdTask();
        manager.createTask(task);
        assertEquals(task, manager.getTaskId(idTask).get());
    }


    @Test
    void getTaskIdByListIsEmptyTest() {
        final int idTask = task.getIdTask();
        assertFalse(manager.getTaskId(idTask).isPresent());
    }


    @Test
    void getTaskIdByWrongIdTest() {
        final int idTask = random.nextInt(2300);
        assertFalse(manager.getTaskId(idTask).isPresent());
    }


    @Test
    void getSubtaskIdTest() {

        manager.createSubtask(subtaskFirst);
        int idSubtask = subtaskFirst.getIdTask();
        assertEquals(subtaskFirst, manager.getSubtaskId(idSubtask).get());
    }


    @Test
    void getSubtaskIdByListIsEmptyTest() {
        int idSubtask = subtaskFirst.getIdTask();
        assertFalse(manager.getSubtaskId(idSubtask).isPresent());
    }

    @Test
    void getSubtaskIdByWrongIdTest() {
        int idSubtask = random.nextInt(2300);
        assertFalse(manager.getSubtaskId(idSubtask).isPresent());
    }

    @Test
    void getEpiIdTest() {
        int idEpic = epic.getIdTask();
        manager.createEpic(epic);
        assertEquals(epic, manager.getEpicId(idEpic).get());

    }


    @Test
    void getEpiIdByListIsEmptyTest() {
        int idEpic = epic.getIdTask();
        assertFalse(manager.getEpicId(idEpic).isPresent());

    }

    @Test
    void getEpiIdByWrongIdTest() {
        int idEpic = random.nextInt(2300);
        assertFalse(manager.getEpicId(idEpic).isPresent());
    }

    @Test
    void getHistoryTest() {
        ArrayList<Task> history = new ArrayList<>();
        manager.createEpic(epic);
        manager.createTask(task);
        manager.getTaskId(task.getIdTask());
        history.add(task);
        manager.getEpicId(epic.getIdTask());
        history.add(epic);
        assertEquals(manager.getHistory(), history);
    }


    @Test
    void getHistoryByListTaskIsEmptyTest() {
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void getListTaskTest() {
        Task task1 = new Task("task", "new", Status.IN_PROGRESS, Duration.ofMinutes(50));
        Task task2 = new Task("task2", "new2", Status.DONE, Duration.ofMinutes(25));
        manager.createTask(task1);
        manager.createTask(task2);
        assertFalse(manager.getListTask().isEmpty());
        assertEquals(2, manager.getListTask().size());
    }

    @Test
    void getListTaskByNotTaskTest() {
        assertTrue(manager.getListTask().isEmpty());
    }


    @Test
    void getListEpicTest() {
        Epic epic1 = new Epic();
        Epic epic2 = new Epic();
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        assertFalse(manager.getListEpic().isEmpty());
        assertEquals(2, manager.getListEpic().size());
    }


    @Test
    void getListEpicByNotEpicTest() {
        assertTrue(manager.getListTask().isEmpty());
    }

    @Test
    void getListSubtaskTest() {
        manager.createSubtask(subtaskFirst);
        manager.createSubtask(subtaskSecond);
        assertFalse(manager.getListSubtask().isEmpty());
        assertEquals(2, manager.getListSubtask().size());

    }

    @Test
    void getListSubtaskByNotSubtaskTest() {
        assertTrue(manager.getListSubtask().isEmpty());
    }


    @Test
    void updateStatusEpicNoSubtaskTest() {
        Epic epicNew = new Epic("epic2", "new");
        manager.createEpic(epicNew);
        assertEquals(Status.NEW, epicNew.getStatus());
    }

    @Test
    void updateStatusEpicBySubtaskStatusNewTest() {
        Epic epicNew = new Epic("epic2", "new");
        Subtask sub1 = new Subtask("sub1", "1", Status.NEW, 0, Duration.ofMinutes(52),
                LocalDateTime.of(2000, 1, 15, 14, 15, 00));
        Subtask sub2 = new Subtask("sub2", "2", Status.NEW, 0, Duration.ofMinutes(240),
                LocalDateTime.of(2000, 1, 29, 00, 00, 00));
        manager.createEpic(epicNew);
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        assertEquals(Status.NEW, epicNew.getStatus());
    }


    @Test
    void updateStatusEpicBySubtaskStatusDoneTest() {
        Epic epicNew = new Epic("epic2", "new");
        Subtask sub1 = new Subtask("sub1", "1", Status.DONE, 0, Duration.ofMinutes(52),
                LocalDateTime.of(2022, 1, 17, 14, 15, 00));
        Subtask sub2 = new Subtask("sub2", "2", Status.DONE, 0, Duration.ofMinutes(240),
                LocalDateTime.of(2022, 1, 33, 12, 00, 00));
        manager.createEpic(epicNew);
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        assertEquals(Status.DONE, epicNew.getStatus());

    }


    @Test
    void updateStatusEpicBySubtaskStatusDoneAndNewTest() {
        Epic epicNew = new Epic("epic2", "new");
        Subtask sub1 = new Subtask("sub1", "1", Status.NEW, 0, Duration.ofMinutes(52),
                LocalDateTime.of(2022, 1, 1, 14, 15, 00));
        Subtask sub2 = new Subtask("sub2", "2", Status.DONE, 0, Duration.ofMinutes(240),
                LocalDateTime.of(2022, 1, 4, 00, 00, 00));
        manager.createEpic(epicNew);
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        assertEquals(Status.IN_PROGRESS, epicNew.getStatus());

    }


    @Test
    void updateStatusEpicBySubtaskStatusInProgressTest() {
        Epic epicNew = new Epic("epic2", "new");
        Subtask sub1 = new Subtask("sub1", "1", Status.IN_PROGRESS, 0, Duration.ofMinutes(52),
                LocalDateTime.of(2022, 11, 7, 14, 15, 00));
        Subtask sub2 = new Subtask("sub2", "2", Status.IN_PROGRESS, 0, Duration.ofMinutes(240),
                LocalDateTime.of(2022, 7, 7, 00, 00, 00));
        manager.createEpic(epicNew);
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        assertEquals(Status.IN_PROGRESS, epicNew.getStatus());

    }


}
