package http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.FileBackedTasksManager;
import task.Epic;
import task.Subtask;
import task.Task;

import java.util.*;


public class HttpTaskManager extends FileBackedTasksManager {

    private final Gson gson;
    private KVTaskClient client;

    public HttpTaskManager(int port) {
        gson = new Gson();
        client = new KVTaskClient(port);
    }


    @Override
    protected void save() {
        client.put("task", gson.toJson(mapTask));
        client.put("subtask", gson.toJson(mapSubtask));
        client.put("epic", gson.toJson(mapEpic));
        client.put("history", gson.toJson(getHistory()));
        client.put("tasks", gson.toJson(getPrioritizedTasks()));


    }

    @Override
    public void load() {
        var jsonPrioritizedTasks = client.load("tasks");
        var prioritizedTaskType = new TypeToken<Set<Task>>(){}.getType();
       Set <Task> priorityTasks = gson.fromJson(jsonPrioritizedTasks, prioritizedTaskType);
        getPrioritizedTasks().addAll(priorityTasks);

        var gsonHistory = client.load("tasks/history");
        var historyType = new TypeToken<List<Task>>(){}.getType();
        List<Task> history = gson.fromJson(gsonHistory, historyType);
        if (history.isEmpty()||history==null) {getHistory().addAll(Collections.emptyList());}
        else {getHistory().addAll(history);}

        var jsonTasks = client.load("tasks/task");
        var taskType = new TypeToken<Map<Integer, Task>>(){}.getType();
       Map<Integer, Task> tasks = gson.fromJson(jsonTasks, taskType);
        getListTask().putAll(tasks);

        var jsonEpics = client.load("tasks/epic");
        var epicType = new TypeToken<Map<Integer, Epic>>(){}.getType();
        Map<Integer, Epic> epics = gson.fromJson(jsonEpics, epicType);
        getListEpic().putAll(epics);

        var jsonSubtasks = client.load("tasks/subtask");
        var subtaskType = new TypeToken<Map<Integer, Subtask>>(){}.getType();
        Map<Integer, Subtask> subtasks = gson.fromJson(jsonSubtasks, subtaskType);
        getListSubtask().putAll(subtasks);
    }
}
