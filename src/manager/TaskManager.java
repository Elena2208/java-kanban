package manager;

import task.*;

import java.util.List;
import java.util.Optional;

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

    Optional<Epic> getEpiId(int id);

    List<Task> getHistory();

}
