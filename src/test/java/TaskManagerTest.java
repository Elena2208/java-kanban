import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected static TaskManager manager ;
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
        int epicId;
        manager.createEpic(epic);
        epicId = epic.getIdTask();
        final Epic epicNew = manager.getEpicId(epicId).get();
        assertNotNull(epicNew, "Эпик не найден");
        assertEquals(epic, epicNew, "Эпики не совпадают");
    }

    @Test
    void createTaskTest() {
        int taskId;
        manager.createTask(task);
        taskId = task.getIdTask();
        final Task taskNew = manager.getTaskId(taskId).get();
        assertNotNull(taskNew, "Задача не найдена");
        assertEquals(task, taskNew, "Задачи  не совпадают");
    }

    @Test
    void createSubtaskTest() {
        int subtaskId;
        manager.createEpic(epic);
        manager.createSubtask(subtaskSecond);
        subtaskId = subtaskSecond.getIdTask();
        final Subtask subtaskNew = manager.getSubtaskId(subtaskId).get();
        assertNotNull(subtaskNew, "Подзадача не найдена");
        assertEquals(subtaskSecond, subtaskNew, "Подзадачи не совпадают");
    }


    @Test
    void updateTaskTest() {
        int idTask;
        manager.createTask(task);
        idTask = task.getIdTask();
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
        int idEpic;
        manager.createEpic(epic);
        idEpic = epic.getIdTask();
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
        int idSubtask;
        manager.createEpic(epic);
        manager.createSubtask(subtaskSecond);
        idSubtask = subtaskSecond.getIdTask();
        manager.updateSubtask(idSubtask, subtaskFirst);
        assertEquals(manager.getSubtaskId(idSubtask).get(), subtaskFirst);
    }

    @Test
    void updateSubtaskByNoSubtaskTest() {
        int idSubtask=subtaskFirst.getIdTask();
        manager.createEpic(epic);
        manager.updateSubtask(idSubtask,subtaskSecond);
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
        final int  idSubtask;
        int idEpic;
        manager.createEpic(epic);
        manager.createSubtask(subtaskFirst);
        idSubtask = subtaskFirst.getIdTask();
        idEpic = subtaskFirst.getIdEpic();
        manager.deleteSubtaskById(idSubtask);
        assertFalse(manager.getSubtaskId(idSubtask).isPresent());
        assertFalse(epic.getIdSubtask().contains(subtaskFirst.getIdTask()));
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
        final int idTask;
        manager.createTask(task);
        idTask = task.getIdTask();
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
    void getEpicIdTest() {
        int idEpic;
        manager.createEpic(epic);
        idEpic = epic.getIdTask();
        assertEquals( manager.getEpicId(idEpic).get(), epic);

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
        Task task1 = new Task("task", "new", Status.IN_PROGRESS, 50);
        Task task2 = new Task("task2", "new2", Status.DONE, 25);
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
        manager.createEpic(epic);
        assertFalse(manager.getListEpic().isEmpty());
        assertEquals(1, manager.getListEpic().size());
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
        Subtask sub1 = new Subtask("sub1", "1", Status.NEW, 0, 52,
                LocalDateTime.of(2000, 1, 15, 14, 15, 00));
        Subtask sub2 = new Subtask("sub2", "2", Status.NEW, 0, 240,
                LocalDateTime.of(2000, 1, 29, 00, 00, 00));
        manager.createEpic(epicNew);
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        assertEquals(Status.NEW, epicNew.getStatus());
    }


    @Test
    void updateStatusEpicBySubtaskStatusDoneTest() {
        manager.createEpic(epic);
        subtaskFirst.setStatus(Status.DONE);
        subtaskSecond.setStatus(Status.DONE);
        manager.createSubtask(subtaskFirst);
        manager.createSubtask(subtaskSecond);
        assertEquals(Status.DONE, epic.getStatus());

    }


    @Test
    void updateStatusEpicBySubtaskStatusDoneAndNewTest() {
        manager.createEpic(epic);
        subtaskFirst.setStatus(Status.DONE);
        subtaskSecond.setStatus(Status.NEW);
        manager.createSubtask(subtaskFirst);
        manager.createSubtask(subtaskSecond);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());

    }


    @Test
    void updateStatusEpicBySubtaskStatusInProgressTest() {
        manager.createEpic(epic);
        subtaskFirst.setStatus(Status.IN_PROGRESS);
        subtaskSecond.setStatus(Status.IN_PROGRESS);
        manager.createSubtask(subtaskFirst);
        manager.createSubtask(subtaskSecond);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());

    }


}
