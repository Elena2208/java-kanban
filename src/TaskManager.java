import task.*;

import java.util.*;

public class TaskManager {
    private static int idInc;
    private HashMap<Integer, Task> mapTask;
    private HashMap<Integer, Subtask> mapSubtask;
    private HashMap<Integer, Epic> mapEpic;

    public TaskManager() {
        mapEpic = new HashMap<>();
        mapTask = new HashMap<>();
        mapSubtask = new HashMap<>();
    }

    public static int getIdInc() {
        return idInc++;
    }

    public Map<Object, Object> getListTask() {
        return mapTask.isEmpty() ? Collections.emptyMap() : new HashMap<>(mapTask);
    }

    public Map<Object, Object> getListEpic() {
        return mapEpic.isEmpty() ? Collections.emptyMap() : new HashMap<>(mapEpic);
    }

    public Map<Object, Object> getListSubtask() {
        return mapSubtask.isEmpty() ? Collections.emptyMap() : new HashMap<>(mapSubtask);
    }

    public void clearTask() {
        mapTask.clear();
    }

    public void clearSubtask() {
        for (Subtask subtask : mapSubtask.values()) {
            int idEpic = subtask.getIdEpic();
            if (mapEpic.get(idEpic).getIdSubtask().contains(subtask.getIdTask())) {
                mapEpic.get(idEpic).getIdSubtask().remove((Integer) subtask.getIdTask());
            }
        }
        mapSubtask.clear();
    }

    public void clearEpic() {
        mapSubtask.clear();
        mapEpic.clear();
    }

    public Optional<Task> getTaskId(int id) {
        return Optional.ofNullable(mapSubtask.get(id));
    }

    public Optional<Subtask> getSubtask(int id) {
        return Optional.ofNullable(mapSubtask.get(id));
    }

    public Optional<Epic> getEpic(int id) {
        return Optional.ofNullable(mapEpic.get(id));
    }

    public void createEpic(Epic epic) {
        if (epic != null) {
            // epic.setStatus(updateStatusEpic(epic.getIdTask()));
            mapEpic.put(epic.getIdTask(), epic);
        } else System.out.println("Эпик не создан");
    }

    public void createTask(Task task) {
        if (task != null) mapTask.put(task.getIdTask(), task);
        else System.out.println("Задача не создана");
    }

    public void createSubtask(Subtask subtask) {
        if (subtask != null) {
            if (mapEpic.containsKey(subtask.getIdEpic())) {
                mapEpic.get(subtask.getIdEpic()).setStatus(getUpdateStatusEpic(subtask.getIdEpic()));
                mapSubtask.put(subtask.getIdTask(), subtask);
                mapEpic.get(subtask.getIdEpic()).getIdSubtask().add(subtask.getIdTask());
            } else {
                System.out.println("Эпик  с таким id не найден");
            }
        } else {
            System.out.println("Передан пустой объект. Подзадача не создана");
        }
    }

    public void updateTask(int idTask, Task task) {
        if (mapTask.containsKey(idTask)) {
            mapTask.put(idTask, task);
        }
    }

    public void updateEpic(int idEpic, Epic epic) {
        if (mapEpic.containsKey(idEpic)) {
            mapEpic.put(idEpic, epic);
        }
    }

    public void updateSubtask(int idSubtask, Subtask subtask) {
        if (mapSubtask.containsKey(idSubtask)) {
            mapSubtask.put(idSubtask, subtask);
            mapEpic.get(subtask.getIdEpic()).setStatus(getUpdateStatusEpic(subtask.getIdEpic()));
        }
    }

    public void deleteTaskById(int idTask) {
        mapTask.remove(idTask);
    }

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
            mapSubtask.remove(idSubtask);
        }
    }

    public void deleteEpicById(int idEpic) {
        if (mapEpic.containsKey(idEpic)) {
            for (int index : mapEpic.get(idEpic).getIdSubtask()) {
                if (mapSubtask.containsKey(index)) {
                    mapSubtask.remove(index);
                }
            }
            mapEpic.remove(idEpic);
        } else System.out.println("Эпик не удален");
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

    public Status getUpdateStatusEpic(int idEpic) {
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

    public void counterStatus(int idSubtask) {
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
