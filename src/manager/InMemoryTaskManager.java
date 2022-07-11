package manager;

import task.*;

import java.util.*;


public class InMemoryTaskManager implements TaskManager {

    protected static int idInc;
    protected HashMap<Integer, Task> mapTask;
    protected HashMap<Integer, Subtask> mapSubtask;
    protected HashMap<Integer, Epic> mapEpic;
    protected HistoryManager historyManager;


    public InMemoryTaskManager() {
        mapEpic = new HashMap<>();
        mapTask = new HashMap<>();
        mapSubtask = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    private static int getIdInc() {
        return idInc++;
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public Map<Integer, Task> getListTask() {
        return mapTask.isEmpty() ? Collections.emptyMap() : new HashMap<>(mapTask);
    }

    public Map<Integer, Epic> getListEpic() {
        return mapEpic.isEmpty() ? Collections.emptyMap() : new HashMap<>(mapEpic);
    }

    public Map<Integer, Subtask> getListSubtask() {
        return mapSubtask.isEmpty() ? Collections.emptyMap() : new HashMap<>(mapSubtask);
    }

    @Override
    public void clearTask() {
        for (int id : mapTask.keySet()) {
            historyManager.remove(id);
        }
        mapTask.clear();
    }


    @Override
    public void clearSubtask() {
        for (Subtask subtask : mapSubtask.values()) {
            int idEpic = subtask.getIdEpic();
            if (mapEpic.get(idEpic).getIdSubtask().contains(subtask.getIdTask())) {
                mapEpic.get(idEpic).getIdSubtask().remove((Integer) subtask.getIdTask());
            }
            historyManager.remove(subtask.getIdTask());
        }
        mapSubtask.clear();
    }


    @Override
    public void clearEpic() {
        for (int id : mapEpic.keySet()) {
            historyManager.remove(id);
        }
        for (Subtask subtask : mapSubtask.values()) {
            historyManager.remove(subtask.getIdTask());
        }
        mapSubtask.clear();
        mapEpic.clear();
    }


    @Override
    public Optional<Task> getTaskId(int id) {
        if (mapTask.get(id) != null) {
            historyManager.add(mapTask.get(id));
        }
        return Optional.ofNullable(mapTask.get(id));
    }

    @Override
    public Optional<Subtask> getSubtaskId(int id) {
        if (mapSubtask.get(id) != null) {
            historyManager.add(mapSubtask.get(id));
        }
        return Optional.ofNullable(mapSubtask.get(id));
    }

    @Override
    public Optional<Epic> getEpiId(int id) {
        if (mapEpic.get(id) != null) {
            historyManager.add(mapEpic.get(id));
        }
        return Optional.ofNullable(mapEpic.get(id));
    }


    @Override
    public void createEpic(Epic epic) {
        if (epic != null) {
            if (epic.getIdTask() == 0) {
                epic.setIdTask(getIdInc());
            }
            mapEpic.put(epic.getIdTask(), epic);
        } else {
            System.out.println("Эпик не создан");
        }
    }


    @Override
    public void createTask(Task task) {
        if (task != null) {
            if (task.getIdTask() == 0) {
                task.setIdTask(getIdInc());
            }
            mapTask.put(task.getIdTask(), task);
        } else {
            System.out.println("Задача не создана");
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (subtask != null) {
            if (subtask.getIdTask() == 0) {
                subtask.setIdTask(getIdInc());
            }
            mapSubtask.put(subtask.getIdTask(), subtask);
            if (mapEpic.containsKey(subtask.getIdEpic())) {
                mapEpic.get(subtask.getIdEpic()).setStatus(getUpdateStatusEpic(subtask.getIdEpic()));
                mapEpic.get(subtask.getIdEpic()).getIdSubtask().add(subtask.getIdTask());
            }
        } else {
            System.out.println("Задача не создана");
        }
    }


    @Override
    public void updateTask(int idTask, Task task) {
        if (mapTask.containsKey(idTask)) {
            task.setIdTask(idTask);
            mapTask.put(idTask, task);
        }
    }


    @Override
    public void updateEpic(int idEpic, Epic epic) {
        if (mapEpic.containsKey(idEpic)) {
            epic.setIdTask(idEpic);
            mapEpic.put(idEpic, epic);
        }
    }


    @Override
    public void updateSubtask(int idSubtask, Subtask subtask) {
        if (mapSubtask.containsKey(idSubtask)) {
            subtask.setIdTask(idSubtask);
            mapSubtask.put(idSubtask, subtask);
            mapEpic.get(subtask.getIdEpic()).setStatus(getUpdateStatusEpic(subtask.getIdEpic()));
        }
    }


    @Override
    public void deleteTaskById(int idTask) {
        if (mapTask.containsKey(idTask)) {
            historyManager.remove(idTask);
            mapTask.remove(idTask);
        }
    }


    @Override
    public void deleteSubtaskById(int idSubtask) {
        if (mapSubtask.containsKey(idSubtask)) {
            int idEpic = mapSubtask.get(idSubtask).getIdEpic();
            ArrayList<Integer> list = mapEpic.get(idEpic).getIdSubtask();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == idSubtask) {
                    list.remove(list.get(i));
                }
            }
            mapEpic.get(idEpic).setStatus(getUpdateStatusEpic(idEpic));
            historyManager.remove(idSubtask);
            mapSubtask.remove(idSubtask);
        }
    }


    @Override
    public void deleteEpicById(int idEpic) {
        if (mapEpic.containsKey(idEpic)) {
            for (int index : mapEpic.get(idEpic).getIdSubtask()) {
                if (mapSubtask.containsKey(index)) {
                    historyManager.remove(index);
                    mapSubtask.remove(index);
                }
            }
            historyManager.remove(idEpic);
            mapEpic.remove(idEpic);
        } else {
            System.out.println("Эпик не удален");
        }
    }


    public HashMap<Integer, Subtask> getListSubtaskEpic(int idEpic) {
        HashMap<Integer, Subtask> list = new HashMap<>();
        for (int index : mapEpic.get(idEpic).getIdSubtask()) {
            if (mapSubtask.containsKey(index)) {
                list.put(index, mapSubtask.get(index));
            }
        }
        return list;
    }


    private Status getUpdateStatusEpic(int idEpic) {
        mapEpic.get(idEpic).setCounterInProgress(0);
        mapEpic.get(idEpic).setCounterDone(0);
        mapEpic.get(idEpic).setCounterNew(0);
        ArrayList<Integer> list = mapEpic.get(idEpic).getIdSubtask();
        for (int i = 0; i < list.size(); i++) {
            counterStatus(list.get(i));
        }
        boolean statusNew = list.size() == mapEpic.get(idEpic).getCounterNew();
        boolean statusDone = list.size() == mapEpic.get(idEpic).getCounterDone();
        if (list.isEmpty() || statusNew) {
            return Status.NEW;
        } else if (statusDone) {
            return Status.DONE;
        } else {
            return Status.IN_PROGRESS;
        }
    }


    private void counterStatus(int idSubtask) {
        Subtask subtask = mapSubtask.get(idSubtask);
        Epic epic = mapEpic.get(subtask.getIdEpic());
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









