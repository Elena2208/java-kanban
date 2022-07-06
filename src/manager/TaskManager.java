package manager;

import task.*;

import java.io.IOException;
import java.util.*;

public interface TaskManager {

    void clearTask();

    void clearSubtask();

    void clearEpic();

    void createTask(Task task);

    void updateTask(int idTask, Task task);

    void deleteTaskById(int idTask);

    Optional<Task> getTaskId(int id);

    List<Task> getHistory();

    Map<Integer, Task> getListTask();

    Map<Integer, Epic> getListEpic();

    Map<Integer, Subtask> getListSubtask();

    HashMap<Integer, Task> getListSubtaskEpic(int idTask);

}
