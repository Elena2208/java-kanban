package manager;

import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.System.*;


public class InMemoryTaskManager implements TaskManager {


    Comparator<Task> nullFriendly;
    protected static int idInc;
    protected HashMap<Integer, Task> mapTask;
    protected HashMap<Integer, Subtask> mapSubtask;
    protected HashMap<Integer, Epic> mapEpic;
    protected HistoryManager historyManager;
    protected Set<Task> treeSet;
    private final String textError = "Пересечение времени выполнения задачи";


    public InMemoryTaskManager() {
        idInc = 0;
        mapEpic = new HashMap<>();
        mapTask = new HashMap<>();
        mapSubtask = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        nullFriendly = Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()));
        treeSet = new TreeSet<>(nullFriendly.thenComparing(Task::getIdTask));
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
    public Optional<Epic> getEpicId(int id) {
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
            out.println("Эпик не создан");
        }
    }


    @Override
    public void createTask(Task task) {
        if (task != null) {
            if (intersectionTimeTask(task)) {
                out.println(textError);
                return;
            } else {
                if (task.getIdTask() == 0) {
                    task.setIdTask(getIdInc());
                }
                treeSet.add(task);
                mapTask.put(task.getIdTask(), task);
            }
        } else {
            out.println("Задача не создана");
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (subtask != null) {
            if (intersectionTimeTask(subtask)) {
                out.println(textError);
                return;
            } else {
                if (subtask.getIdTask() == 0) {
                    subtask.setIdTask(getIdInc());
                }
                treeSet.add(subtask);
                mapSubtask.put(subtask.getIdTask(), subtask);
            }
            if (mapEpic.containsKey(subtask.getIdEpic())) {
                mapEpic.get(subtask.getIdEpic()).getIdSubtask().add(subtask.getIdTask());
                mapEpic.get(subtask.getIdEpic()).setStatus(getUpdateStatusEpic(subtask.getIdEpic()));
                if (subtask.getDuration()!=0) {
                    setDurationEpic(subtask.getIdEpic());
                }
                if (subtask.getStartTime() != null) {
                    setStartTimeEpic(subtask.getIdEpic());
                    setEndTimeEpic(subtask.getIdEpic());
                }
            }
        } else {
            out.println("Задача не создана");
        }
    }


    @Override
    public void updateTask(int idTask, Task task) {
        if (mapTask.containsKey(idTask)) {
            task.getEndTime();
            if (intersectionTimeTask(task)) {
                out.println(textError);
                return;
            } else {
                task.setIdTask(idTask);
                mapTask.put(idTask, task);
            }
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
            subtask.getEndTime();
            if (intersectionTimeTask(subtask)) {
                out.println(textError);
                return;
            } else {
                subtask.setIdTask(idSubtask);
                mapSubtask.put(idSubtask, subtask);
                mapEpic.get(subtask.getIdEpic()).setStatus(getUpdateStatusEpic(subtask.getIdEpic()));
                setStartTimeEpic(subtask.getIdEpic());
                setEndTimeEpic(subtask.getIdEpic());
                setDurationEpic(subtask.getIdEpic());
            }
        }
    }


    @Override
    public void deleteTaskById(int idTask) {
        if (mapTask.containsKey(idTask)) {
            historyManager.remove(idTask);
            treeSet.remove(mapTask.get(idTask));
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
            setStartTimeEpic(mapSubtask.get(idSubtask).getIdEpic());
            setEndTimeEpic(mapSubtask.get(idSubtask).getIdEpic());
            setDurationEpic(mapSubtask.get(idSubtask).getIdEpic());
            historyManager.remove(idSubtask);
            treeSet.remove(mapSubtask.get(idSubtask));
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
            out.println("Эпик не удален");
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

    @Override
    public Set<Task> getPrioritizedTasks() {
        return treeSet;
    }

    @Override
    public void load() {

    }


    private Status getUpdateStatusEpic(int idEpic) {
        mapEpic.get(idEpic).setCounterInProgress(0);
        mapEpic.get(idEpic).setCounterDone(0);
        mapEpic.get(idEpic).setCounterNew(0);
        if (mapEpic.get(idEpic).getIdSubtask().isEmpty()) {
            return Status.NEW;
        } else {
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


    protected void setEndTimeEpic(int idEpic) {
        if (mapEpic.containsKey(idEpic)) {
            for (Integer i : mapEpic.get(idEpic).getIdSubtask()) {
                if (mapSubtask.get(i).getStartTime() != null) {
                    mapSubtask.get(i).getEndTime();
                }
            }
            List<Subtask> subtaskList = mapSubtask.values().stream()
                    .filter(subtask -> subtask.getIdEpic() == idEpic)
                    .sorted(Comparator.comparing(Task::getStartTime))
                    .collect(Collectors.toList());
            mapEpic.get(idEpic).setEndTime(subtaskList.get(subtaskList.size() - 1).getEndTime());
        }
    }


    protected void setDurationEpic(int idEpic) {
        if (mapEpic.containsKey(idEpic)) {
            List<Subtask> subtaskList = mapSubtask.values().stream()
                    .filter(subtask -> subtask.getIdEpic() == idEpic)
                    .collect(Collectors.toList());
            long sumMinute = 0;
            for (Subtask subtask : subtaskList) {
                sumMinute += subtask.getDuration();
            }
            mapEpic.get(idEpic).setDuration(sumMinute);
        }
    }

    protected void setStartTimeEpic(int idEpic) {
        if (mapEpic.containsKey(idEpic)) {
            List<Subtask> subtaskList = mapSubtask.values().stream().
                    filter(subtask -> subtask.getIdEpic() == idEpic)
                    .sorted(Comparator.comparing(Task::getStartTime))
                    .collect(Collectors.toList());
            if (subtaskList.isEmpty()) {
                mapEpic.get(idEpic).setStartTime(LocalDateTime.now());
            } else {
                mapEpic.get(idEpic).setStartTime(subtaskList.get(0).getStartTime());
            }
        }
    }


    private boolean intersectionTimeTask(Task task) {
        Set<Task> sortedTask = getPrioritizedTasks();
        Iterator<Task> iterator = sortedTask.iterator();
        boolean sign = false;
        Task itTask;
        if (!sortedTask.isEmpty()) {
            while (iterator.hasNext()) {
                itTask = iterator.next();
                if (task.getStartTime() != null && itTask.getStartTime() != null) {
                    if (!task.getStartTime().isAfter(itTask.getEndTime()))
                        if (task.getEndTime().isAfter(itTask.getStartTime())) {
                            sign = true;
                        }
                }
            }
        }
        return sign;
    }
}











