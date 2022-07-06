package manager;

import task.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected static int idInc;
    protected HashMap<Integer, Task> mapTasks;
    protected HistoryManager historyManager;

    public InMemoryTaskManager() {
        mapTasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private static int getIdInc() {
        return idInc++;
    }

    public Map<Integer, Task> getListTask() {
        Map<Integer, Task> mapTask = new HashMap<>();
        for (Map.Entry<Integer, Task> entry : mapTasks.entrySet()) {
            if (entry.getValue().getTypeTask().toString().equals("TASK")) {
                mapTask.put(entry.getKey(), entry.getValue());
            }
        }
        return mapTask.isEmpty() ? Collections.emptyMap() : mapTask;
    }

    public Map<Integer, Epic> getListEpic() {
        Map<Integer, Epic> mapEpic = new HashMap<>();
        for (Map.Entry<Integer, Task> entry : mapTasks.entrySet()) {
            if (entry.getValue().getTypeTask().toString().equals("EPIC")) {
                mapEpic.put(entry.getKey(), (Epic) entry.getValue());
            }
        }
        return mapEpic.isEmpty() ? Collections.emptyMap() : mapEpic;
    }

    public Map<Integer, Subtask> getListSubtask() {
        Map<Integer, Subtask> mapSubtask = new HashMap<>();
        for (Map.Entry<Integer, Task> entry : mapTasks.entrySet()) {
            if (entry.getValue().getTypeTask().toString().equals("SUBTASK")) {
                mapSubtask.put(entry.getKey(), (Subtask) entry.getValue());
            }
        }
        return mapSubtask.isEmpty() ? Collections.emptyMap() : mapSubtask;
    }

    @Override
    public void clearTask() {
        int size = mapTasks.size();
        for (int id = 0; id < size; id++)
            if (mapTasks.get(id).getTypeTask().toString().equals("TASK")) {
                historyManager.remove(id);
                mapTasks.remove(id);
            }
    }

    @Override
    public void clearSubtask() {
        int size = mapTasks.size();
        for (int id = 0; id < size; id++) {
            if (mapTasks.get(id).getTypeTask().toString().equals("SUBTASK")) {
                Subtask subtask = (Subtask) mapTasks.get(id);
                int idEpic = subtask.getIdEpic();
                Epic epic = (Epic) mapTasks.get(idEpic);
                if (epic.getIdSubtask().contains(subtask.getIdTask())) {
                    epic.getIdSubtask().remove((Integer) subtask.getIdTask());
                }
                historyManager.remove(id);
                mapTasks.remove(id);
            }
        }
    }

    @Override
    public void clearEpic() {
        int size = mapTasks.size();
        for (int id = 0; id < size; id++) {
            if (mapTasks.get(id).getTypeTask().toString().equals("EPIC") || mapTasks.get(id).getTypeTask().toString().equals("SUBTASK")) {
                historyManager.remove(id);
                mapTasks.remove(id);
            }
        }
    }

    @Override
    public Optional<Task> getTaskId(int id) {
        if (mapTasks.get(id) != null) {
            historyManager.add(mapTasks.get(id));
        }
        return Optional.ofNullable(mapTasks.get(id));
    }

    @Override
    public void createTask(Task task) {
        if (task != null) {
            if (task.getIdTask() == 0) {
                task.setIdTask(getIdInc());
            }
            mapTasks.put(task.getIdTask(), task);
            if (task.getTypeTask().toString().equals("SUBTASK")) {
                Subtask subtask = (Subtask) task;
                if (mapTasks.containsKey(subtask.getIdEpic())) {
                    mapTasks.get(subtask.getIdEpic()).setStatus(getUpdateStatusEpic(subtask.getIdEpic()));
                    Epic epic = (Epic) mapTasks.get(subtask.getIdEpic());
                    epic.getIdSubtask().add(subtask.getIdTask());
                }
            }
        } else System.out.println("Задача не создана");
    }

    @Override
    public void updateTask(int idTask, Task task) {
        if (mapTasks.containsKey(idTask)) {
            task.setIdTask(idTask);
            mapTasks.put(idTask, task);
            if (task.getTypeTask().toString().equals("SUBTASK")) {
                Subtask subtask = (Subtask) task;
                mapTasks.get(subtask.getIdEpic()).setStatus(getUpdateStatusEpic(subtask.getIdEpic()));
            }
        } else {
            System.out.println("Задача не найдена");
        }
    }

    @Override
    public void deleteTaskById(int idTask) {
        if (mapTasks.containsKey(idTask)) {
            if (mapTasks.get(idTask).getTypeTask().toString().equals("SUBTASK")) {
                Subtask subtask = (Subtask) mapTasks.get(idTask);
                int idEpic = subtask.getIdEpic();
                Epic epic = (Epic) mapTasks.get(idEpic);
                ArrayList<Integer> list = epic.getIdSubtask();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) == idTask) {
                        list.remove(list.get(i));
                    }
                }
                epic.setStatus(getUpdateStatusEpic(idEpic));
            }
            if (mapTasks.get(idTask).getTypeTask().toString().equals("EPIC")) {
                Epic epic = (Epic) mapTasks.get(idTask);
                for (int index : epic.getIdSubtask()) {
                    if (mapTasks.containsKey(index)) {
                        historyManager.remove(index);
                        mapTasks.remove(index);
                    }
                }
            }
            historyManager.remove(idTask);
            mapTasks.remove(idTask);
        } else {
            System.out.println("Задача не найдена");
        }
    }

    public HashMap<Integer, Task> getListSubtaskEpic(int idTask) {
        HashMap<Integer, Task> list = new HashMap<>();
        Epic epic = (Epic) mapTasks.get(idTask);
        for (int index : epic.getIdSubtask()) {
            if (mapTasks.containsKey(index)) {
                list.put(index, mapTasks.get(index));
            }
        }
        return list;
    }

    private Status getUpdateStatusEpic(int idEpic) {
        Epic epic = (Epic) mapTasks.get(idEpic);
        epic.setCounterInProgress(0);
        epic.setCounterDone(0);
        epic.setCounterNew(0);
        ArrayList<Integer> list = epic.getIdSubtask();
        for (int i = 0; i < list.size(); i++) {
            counterStatus(list.get(i));
        }
        boolean statusNew = list.size() == epic.getCounterNew();
        boolean statusDone = list.size() == epic.getCounterDone();
        if (list.isEmpty() || statusNew) {
            return Status.NEW;
        } else if (statusDone) {
            return Status.DONE;
        } else {
            return Status.IN_PROGRESS;
        }
    }

    private void counterStatus(int idTask) {
        Subtask subtask = (Subtask) mapTasks.get(idTask);
        Epic epic = (Epic) mapTasks.get(subtask.getIdEpic());
        switch (subtask.getStatus()) {
            case NEW:
                epic.setCounterNew(epic.getCounterNew() + 1);
                break;
            case DONE:
                epic.setCounterDone(epic.getCounterDone() + 1);
                break;
            case IN_PROGRESS:
                epic.setCounterInProgress(epic.getCounterInProgress() + 1);
                break;
            default:
                break;
        }
    }
}






