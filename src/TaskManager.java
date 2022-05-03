import java.util.*;


public class TaskManager {
    private static int idInc;

    private HashMap<Integer, Task> listTask;
    private HashMap<Integer, Subtask> listSubtask;
    private HashMap<Integer, Epic> listEpic;

    public TaskManager() {
        listEpic = new HashMap<>();
        listTask = new HashMap<>();
        listSubtask = new HashMap<>();
    }

    public static int getIdInc() {
        return idInc++;
    }

    public Map<Object, Object> getListTask() {
        if (!listTask.isEmpty()) return new HashMap<>(listTask);
        else return Collections.emptyMap();
    }

    public Map<Object, Object> getListEpic() {
        if (!listEpic.isEmpty()) return new HashMap<>(listEpic);
        else return Collections.emptyMap();
    }

    public Map<Object, Object> getListSubtask() {
        if (!listSubtask.isEmpty()) return new HashMap<>(listSubtask);
        else return Collections.emptyMap();
    }

    public void clearTask() {
        listTask.clear();
    }

    public void clearSubtask() {
        listSubtask.clear();
    }

    public void clearEpic() {
        listEpic.clear();
    }

    public Optional<Task> getTaskId(int id) {
        if (listTask.containsKey(id)) return Optional.ofNullable(listTask.get(id));
        else {
            System.out.println("Задача с таким id не найдена");
            return Optional.empty();
        }
    }

    public Optional<Subtask> getSubtask(int id) {
        if (listSubtask.containsKey(id)) return Optional.ofNullable(listSubtask.get(id));
        else {
            System.out.println("Подзадача с таким id не найдена");
            return Optional.empty();
        }
    }

    public Optional<Epic> getEpic(int id) {
        if (listEpic.containsKey(id)) return Optional.ofNullable(listEpic.get(id));
        else {
            System.out.println("Эпик с таким id не найден");
            return Optional.empty();
        }
    }

    public void createEpic(Epic epic) {
        if (epic != null) {
            // epic.setStatus(updateStatusEpic(epic.getIdTask()));
            listEpic.put(epic.getIdTask(), epic);
        } else System.out.println("Эпик не создан");

    }

    public void createTask(Task task) {
        if (task != null) listTask.put(task.getIdTask(), task);
        else System.out.println("Задача не создана");
    }

    public void createSubtask(Subtask subtask) {
        if (subtask != null) if (listEpic.containsKey(subtask.getIdEpic())) {
            listEpic.get(subtask.getIdEpic()).setStatus(updateStatusEpic(subtask.getIdEpic()));
            listSubtask.put(subtask.getIdTask(), subtask);
            listEpic.get(subtask.getIdEpic()).getIdSubtask().add(subtask.getIdTask());

        } else {
            System.out.println("Эпик  с таким id не найден");
        }
        else {
            System.out.println("Подзадача не создана");
        }
    }

    public void updateTask(int idTask, Task task) {
        if (listTask.containsKey(idTask)) {
            listTask.get(idTask).setTitle(task.getTitle());
            listTask.get(idTask).setDescription(task.getDescription());
            listTask.get(idTask).setStatus(task.getStatus());
        } else System.out.println("Замена не произошла");
    }

    public void updateEpic(int idEpic, Epic epic) {
        if (listEpic.containsKey(idEpic)) {
            listEpic.get(idEpic).setDescription(epic.getDescription());
            listEpic.get(idEpic).setTitle(epic.getTitle());


        }
    }

    public void updateSubtask(int idSubtask, Subtask subtask) {
        if (listSubtask.containsKey(idSubtask)) {
            listSubtask.get(idSubtask).setDescription(subtask.getDescription());
            listSubtask.get(idSubtask).setTitle(subtask.getTitle());
            listSubtask.get(idSubtask).setStatus(subtask.getStatus());
            listEpic.get(subtask.getIdEpic()).setStatus(updateStatusEpic(subtask.getIdEpic()));
        }
    }

    public void deleteTaskId(int idTask) {
        if (listTask.containsKey(idTask)) {
            listTask.remove(idTask);
        } else System.out.println("Задача не удалена");
    }


    public void deleteSubtaskId(int idSubtask) {
        int idEpic=listSubtask.get(idSubtask).getIdEpic();
        ArrayList <Integer> list=listEpic.get(idEpic).getIdSubtask();
        if (listSubtask.containsKey(idSubtask)) {
            for (int i=0; i<list.size(); i++ ){
                System.out.println(list.get(i));
                if (list.get(i) == idSubtask) {
                    list.remove(list.get(i));

                }

            }
            listEpic.get(idEpic).setStatus(updateStatusEpic(idEpic));
            listSubtask.remove(idSubtask);
        } else System.out.println("Задача не удалена");
    }


    public void deleteEpicId(int idEpic) {
        if (listEpic.containsKey(idEpic)) {
            for (int index : listEpic.get(idEpic).getIdSubtask()) {
                if (listSubtask.containsKey(index)) {
                    listSubtask.remove(index);
                }
            }
            listEpic.remove(idEpic);
        } else System.out.println("Эпик не удален");
    }

    public HashMap<Integer, Subtask> getListSubtaskEpic(int idEpic) {
        HashMap<Integer, Subtask> list = new HashMap<>();
        for (int index : listEpic.get(idEpic).getIdSubtask()) {
            if (listSubtask.containsKey(index)) {
                list.put(index, listSubtask.get(index));
            }
        }
        return list;
    }

    public String updateStatusEpic(int idEpic) {
        listEpic.get(idEpic).setCounterInProgress(0);
        listEpic.get(idEpic).setCounterDone(0);
        listEpic.get(idEpic).setCounterNew(0);

        ArrayList<Integer> list = listEpic.get(idEpic).getIdSubtask();
        for (int i = 0; i < list.size(); i++) {
            counterStatus(list.get(i));
        }
        boolean statusNew = list.size() == listEpic.get(idEpic).getCounterNew();
        boolean statusDone = list.size() == listEpic.get(idEpic).getCounterDone();
        if (list.isEmpty() || statusNew) {
            return "NEW";
        } else {
            if (statusDone) return "DONE";
        }

        return "IN_PROGRESS";
    }

    public void counterStatus(int idSubtask) {
        Subtask subtask = listSubtask.get(idSubtask);
        Epic epic = listEpic.get(subtask.getIdEpic());
        switch (subtask.getStatus()) {
            case "NEW":
                epic.setCounterNew(epic.getCounterNew() + 1);
                break;
            case "DONE":
                epic.setCounterDone(epic.getCounterDone() + 1);
                break;
            case "IN_PROGRESS":
                epic.setCounterInProgress(epic.getCounterInProgress() + 1);
                break;
            default:
                break;

        }
    }
}
