package manager;

import task.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    private String fileName;

    public FileBackedTasksManager() {
        idInc = 0;
    }

    public FileBackedTasksManager(String filename) {
        idInc = 0;
        historyManager = Managers.getDefaultHistory();
        this.fileName = filename;
        if (Files.exists(Paths.get(fileName))) {
            try {
                Files.delete(Paths.get(fileName));
            } catch (IOException exception) {
                System.out.println("Ошибка при попытке удаления существующего файла для пересоздания");
                exception.printStackTrace();
            }
        } else {
            try {
                Files.createFile(Paths.get(fileName));

            } catch (IOException exception) {
                System.out.println("Ошибка при попытке создания файла");
                exception.printStackTrace();
            }
        }
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public static FileBackedTasksManager loadFromFile(File file) {

        FileBackedTasksManager newFileBackedTasksManager = new FileBackedTasksManager();
        newFileBackedTasksManager.setFileName("Tasks.csv");
        try {
            String files = Files.readString(Path.of(String.valueOf(file.getName())));
            String[] line = files.split("\n");
            for (int i = 1; i < line.length - 2; i++) {

                String[] str = line[i].split(",");

                switch (str[1]) {
                    case "TASK":
                        Task task = newFileBackedTasksManager.taskFromString(line[i]);
                        newFileBackedTasksManager.createTask(task);
                        break;
                    case "SUBTASK":
                        Subtask subtask = (Subtask) newFileBackedTasksManager.taskFromString(line[i]);
                        newFileBackedTasksManager.createSubtask(subtask);
                        break;
                    case "EPIC":
                        Epic epic = (Epic) newFileBackedTasksManager.taskFromString(line[i]);
                        newFileBackedTasksManager.createEpic(epic);
                        break;
                    default:
                        ;
                }

            }

            for (Subtask subtask : newFileBackedTasksManager.mapSubtask.values()) {
                Epic epic = newFileBackedTasksManager.mapEpic.get(subtask.getIdEpic());
                epic.getIdSubtask().add(subtask.getIdTask());
            }

            List<Integer> list = FileBackedTasksManager.historyFromString(line[line.length - 1]);

            for (int i = 0; i < list.size(); i++) {
                if (newFileBackedTasksManager.mapEpic.containsKey(i)) {
                    Epic epic = newFileBackedTasksManager.mapEpic.get(i);
                    newFileBackedTasksManager.historyManager.add(epic);
                }
                if (newFileBackedTasksManager.mapTask.containsKey(i)) {
                    Task task = newFileBackedTasksManager.mapTask.get(i);
                    newFileBackedTasksManager.historyManager.add(task);
                }
                if (newFileBackedTasksManager.mapSubtask.containsKey(i)) {
                    Subtask subtask = newFileBackedTasksManager.mapSubtask.get(i);
                    newFileBackedTasksManager.historyManager.add(subtask);
                }


            }


        } catch (IOException exception) {

        }
        return newFileBackedTasksManager;
    }

    public String taskToString(Task task) {
        String line = "";
        switch (task.getTypeTask()) {
            case TASK:
                line = String.join(",", Integer.toString(task.getIdTask()), task.getTypeTask().toString(),
                        task.getTitle(), task.getStatus().toString(), task.getDescription());
                break;
            case EPIC:
                Epic epic = (Epic) task;
                line = String.join(",", Integer.toString(epic.getIdTask()), epic.getTypeTask().toString(),
                        epic.getTitle(), epic.getStatus().toString(), epic.getDescription());
                break;
            case SUBTASK:
                Subtask subtask = (Subtask) task;
                line = String.join(",", Integer.toString(subtask.getIdTask()), subtask.getTypeTask().toString(),
                        subtask.getTitle(), subtask.getStatus().toString(), subtask.getDescription(),
                        Integer.toString(subtask.getIdEpic()));
                break;
            default:
                System.out.println("Не определен тип задачи");
        }
        return line;
    }

    static String historyToString(HistoryManager manager) {
        StringBuilder line = new StringBuilder();

        for (Task task : manager.getHistory()) {
            line.append(task.getIdTask());
            line.append(",");
        }

        return line.toString();
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        if (value.isEmpty()) {
            System.out.println("Передана пустая строка");
            return Collections.emptyList();
        }
        String[] line = value.split(",");
        for (String str : line) {
            if (!str.isEmpty()) history.add(Integer.parseInt(str));
        }
        return history;
    }

    Task taskFromString(String value) {
        Task newTask = new Task();
        if (value.isEmpty()) {
            System.out.println("Передана пустая строка");
            return newTask;
        }
        String[] line = value.split(",");
        switch (line[1]) {
            case "TASK":
                newTask = new Task();
                newTask.setIdTask(Integer.parseInt(line[0]));
                newTask.setTypeTask(TypeTask.TASK);
                newTask.setTitle(line[2]);
                newTask.setStatus(Status.valueOf(line[3]));
                newTask.setDescription(line[4]);
                break;
            case "SUBTASK":
                newTask = new Subtask();
                newTask.setIdTask(Integer.parseInt(line[0]));
                newTask.setTypeTask(TypeTask.SUBTASK);
                newTask.setTitle(line[2]);
                newTask.setStatus(Status.valueOf(line[3]));
                newTask.setDescription(line[4]);
                ((Subtask) newTask).setIdEpic(Integer.parseInt(line[5]));
                break;
            case "EPIC":
                newTask = new Epic();
                newTask.setIdTask(Integer.parseInt(line[0]));
                newTask.setTypeTask(TypeTask.EPIC);
                newTask.setTitle(line[2]);
                newTask.setStatus(Status.valueOf(line[3]));
                newTask.setDescription(line[4]);
                break;
            default:
                System.out.println("Задача не создана");
        }
        return newTask;

    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public List<Task> getHistory() {
        save();
        return super.getHistory();
    }

    @Override
    public Map<Integer, Task> getListTask() {
        return super.getListTask();
    }

    @Override
    public Map<Integer, Epic> getListEpic() {
        return super.getListEpic();
    }

    @Override
    public Map<Integer, Subtask> getListSubtask() {
        return super.getListSubtask();
    }

    @Override
    public void save() {

        try (FileWriter writer = new FileWriter(fileName)) {

            writer.write("id,type,name,status,description,epic" + "\n");
            for (Task task : getListTask().values()) {

                writer.write(taskToString(task) + "\n");
            }
            for (Epic epic : getListEpic().values()) {

                writer.write(taskToString(epic) + "\n");
            }
            for (Subtask subtask : getListSubtask().values()) {

                writer.write(taskToString(subtask) + "\n");
            }
            writer.write("\n");
            writer.write(historyToString(historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException();
        }


    }

    @Override
    public void clearTask() {
        super.clearTask();
        save();
    }

    @Override
    public void clearSubtask() {
        super.clearSubtask();
        save();
    }

    @Override
    public void clearEpic() {
        super.clearEpic();
        save();
    }

    @Override
    public Optional<Task> getTaskId(int id) {
        save();
        return super.getTaskId(id);
    }

    @Override
    public Optional<Subtask> getSubtaskId(int id) {
        save();
        return super.getSubtaskId(id);
    }

    @Override
    public Optional<Epic> getEpiId(int id) {
        save();
        return super.getEpiId(id);
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(int idTask, Task task) {
        super.updateTask(idTask, task);
        save();
    }

    @Override
    public void updateEpic(int idEpic, Epic epic) {
        super.updateEpic(idEpic, epic);
        save();
    }

    @Override
    public void updateSubtask(int idSubtask, Subtask subtask) {
        super.updateSubtask(idSubtask, subtask);
        save();
    }

    @Override
    public void deleteTaskById(int idTask) {
        super.deleteTaskById(idTask);
        save();
    }

    @Override
    public void deleteSubtaskById(int idSubtask) {
        super.deleteSubtaskById(idSubtask);
        save();
    }

    @Override
    public void deleteEpicById(int idEpic) {
        super.deleteEpicById(idEpic);
        save();
    }

    @Override
    public HashMap<Integer, Subtask> getListSubtaskEpic(int idEpic) {
        return super.getListSubtaskEpic(idEpic);
    }

    @Override
    public Status getUpdateStatusEpic(int idEpic) {
        save();
        return super.getUpdateStatusEpic(idEpic);
    }

    @Override
    public void counterStatus(int idSubtask) {
        super.counterStatus(idSubtask);
    }
}
