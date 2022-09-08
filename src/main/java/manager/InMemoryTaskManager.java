package manager;

import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {

    class TaskComparator implements Comparator<Task> {
        @Override
        public int compare(Task o1, Task o2) {
            return o1.getStartTime().compareTo(o2.getStartTime());
        }
    }

    protected static int idInc;
    protected HashMap<Integer, Task> mapTask;
    protected HashMap<Integer, Subtask> mapSubtask;
    protected HashMap<Integer, Epic> mapEpic;
    protected HistoryManager historyManager;
    protected TaskComparator comparator;


    public InMemoryTaskManager() {
        mapEpic = new HashMap<>();
        mapTask = new HashMap<>();
        mapSubtask = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        comparator = new TaskComparator();
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
            System.out.println("Эпик не создан");
        }
    }


    @Override
    public void createTask(Task task) {
        if (task != null) {

            if (task.getStartTime() != null && !task.getDuration().isZero()) {
                task.setEndTime(task.getEndTime());
            }
            try {
                if (intersectionTimeTask(task)) {
                    throw new IntersectionTimeTask("Пересечение времени выполнения задачи");
                } else {
                    if (task.getIdTask() == 0) {
                        task.setIdTask(getIdInc());
                    }
                    mapTask.put(task.getIdTask(), task);
                }
            } catch (IntersectionTimeTask intersectionTimeTask) {
                System.out.println(intersectionTimeTask.getMessage());
                return;
            }
        } else {
            System.out.println("Задача не создана");
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (subtask != null) {

            subtask.setEndTime(subtask.getEndTime());
            try {
                if (intersectionTimeTask(subtask)) {
                    throw new IntersectionTimeTask("Пересечение времени выполнения задачи");
                } else {
                    if (subtask.getIdTask() == 0) {
                        subtask.setIdTask(getIdInc());
                    }
                    mapSubtask.put(subtask.getIdTask(), subtask);

                }
                if (mapEpic.containsKey(subtask.getIdEpic())) {
                    mapEpic.get(subtask.getIdEpic()).getIdSubtask().add(subtask.getIdTask());
                    mapEpic.get(subtask.getIdEpic()).setStatus(getUpdateStatusEpic(subtask.getIdEpic()));
                    getStartTimeEpic(subtask.getIdEpic());
                    getEndTimeEpic(subtask.getIdEpic());
                    getDurationEpic(subtask.getIdEpic());
                }
            } catch (IntersectionTimeTask intersectionTimeTask) {
                System.out.println(intersectionTimeTask.getMessage());
                return;
            }
        } else {
            System.out.println("Задача не создана");
        }
    }


    @Override
    public void updateTask(int idTask, Task task) {
        if (mapTask.containsKey(idTask)) {
            task.setEndTime(task.getEndTime());
            try {
                if (intersectionTimeTask(task)) {
                    throw new IntersectionTimeTask("Пересечение времени выполнения задачи");
                } else {

                    task.setIdTask(idTask);
                    mapTask.put(idTask, task);
                }

            } catch (IntersectionTimeTask intersectionTimeTask) {
                System.out.println(intersectionTimeTask.getMessage());
                return;
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
            subtask.setEndTime(subtask.getEndTime());
            try {
                if (intersectionTimeTask(subtask)) {
                    throw new IntersectionTimeTask("Пересечение времени выполнения задачи");
                } else {
                    subtask.setIdTask(idSubtask);
                    mapSubtask.put(idSubtask, subtask);
                    mapEpic.get(subtask.getIdEpic()).setStatus(getUpdateStatusEpic(subtask.getIdEpic()));
                    getStartTimeEpic(subtask.getIdEpic());
                    getEndTimeEpic(subtask.getIdEpic());
                    getDurationEpic(subtask.getIdEpic());
                }
            } catch (IntersectionTimeTask intersectionTimeTask) {
                System.out.println(intersectionTimeTask.getMessage());
                return;
            }

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
            getStartTimeEpic(mapSubtask.get(idSubtask).getIdEpic());
            getEndTimeEpic(mapSubtask.get(idSubtask).getIdEpic());
            getDurationEpic(mapSubtask.get(idSubtask).getIdEpic());
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

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        TreeSet<Task> treeSet = new TreeSet<>(comparator);
        if (!mapTask.isEmpty() || !mapSubtask.isEmpty()) {

            for (Task task : mapSubtask.values()) {
                task.setStartTime(task.getStartTime());
                treeSet.add(task);
            }
            for (Task task : mapTask.values()) {
                task.setStartTime(task.getStartTime());
                treeSet.add(task);
            }
        }
        return treeSet;

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


    protected void getEndTimeEpic(int idEpic) {
        for (Integer i : mapEpic.get(idEpic).getIdSubtask()) {
            mapSubtask.get(i).setEndTime(mapSubtask.get(i).getEndTime());
        }
        List<Subtask> subtaskList = mapSubtask.values().stream()
                .filter(subtask -> subtask.getIdEpic() == idEpic)
                .sorted(comparator)
                .collect(Collectors.toList());
        mapEpic.get(idEpic).setEndTime(subtaskList.get(subtaskList.size() - 1).getEndTime());

    }


    protected void getDurationEpic(int idEpic) {

        List<Subtask> subtaskList = mapSubtask.values().stream()
                .filter(subtask -> subtask.getIdEpic() == idEpic)
                .collect(Collectors.toList());
        long sumMinute = 0;
        for (Subtask subtask : subtaskList) {
            sumMinute += subtask.getDuration().toMinutes();
        }
        mapEpic.get(idEpic).setDuration(Duration.ofMinutes(sumMinute));

    }

    protected void getStartTimeEpic(int idEpic) {

        List<Subtask> subtaskList = mapSubtask.values().stream().
                filter(subtask -> subtask.getIdEpic() == idEpic)
                .sorted(comparator)
                .collect(Collectors.toList());
        if (subtaskList.isEmpty()) {
            mapEpic.get(idEpic).setStartTime(LocalDateTime.now());
        } else {
            mapEpic.get(idEpic).setStartTime(subtaskList.get(0).getStartTime());
        }
    }

    private boolean intersectionTimeTask(Task task) {
        TreeSet<Task> sortedTask = getPrioritizedTasks();
        Iterator<Task> iterator = sortedTask.iterator();
        boolean sign = false;
        Task itTask;
        if (!sortedTask.isEmpty()) {
            while (iterator.hasNext()) {
                itTask = iterator.next();
                if (!task.getStartTime().isAfter(itTask.getEndTime()))
                    if (task.getEndTime().isAfter(itTask.getStartTime())) {
                        sign = true;
                    }
            }

        }
        return sign;
    }
}











