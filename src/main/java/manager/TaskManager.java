package manager;

import task.*;

import java.util.*;

public interface TaskManager {

    void clearTask();

    void clearSubtask();

    void clearEpic();

    void createEpic(Epic epic);

    void createTask(Task task);

    void createSubtask(Subtask subtask);

    void updateTask(int idTask, Task task);

    void updateEpic(int idEpic, Epic epic);

    void updateSubtask(int idSubtask, Subtask subtask);

    void deleteTaskById(int idTask);

    void deleteSubtaskById(int idSubtask);

    void deleteEpicById(int idEpic);

    Optional<Task> getTaskId(int id);

    Optional<Subtask> getSubtaskId(int id);

    Optional<Epic> getEpicId(int id);

    List<Task> getHistory();

    Map<Integer, Task> getListTask();

    Map<Integer, Epic> getListEpic();

    Map<Integer, Subtask> getListSubtask();

    HashMap<Integer, Subtask> getListSubtaskEpic(int idEpic);

    Set <Task> getPrioritizedTasks();


}