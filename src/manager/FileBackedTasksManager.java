package manager;

import task.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final String fileHeader = "id,type,name,status,description,epic";
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
            String files = Files.readString(Path.of((file.getName())));
            String[] line = files.split("\n");
            for (int i = 1; i < line.length; i++) {
                if (line[i].isBlank()) break;
                String[] str = line[i].split(",");

                switch (str[1]) {
                    case "TASK":
                        Task task = newFileBackedTasksManager.taskFromString(line[i]);
                        newFileBackedTasksManager.createTask(task);
                        break;
                    case "SUBTASK":
                        Subtask subtask = (Subtask) newFileBackedTasksManager.taskFromString(line[i]);
                        newFileBackedTasksManager.createTask(subtask);
                        Epic epic = (Epic) newFileBackedTasksManager.mapTasks.get(subtask.getIdEpic());
                        epic.getIdSubtask().add(subtask.getIdTask());
                        break;
                    case "EPIC":
                        Epic epicNew = (Epic) newFileBackedTasksManager.taskFromString(line[i]);
                        newFileBackedTasksManager.createTask(epicNew);
                        break;
                    default:
                }
            }
            List<Integer> list = historyFromString(line[line.length - 1]);
            for (int i : list) {
                if (newFileBackedTasksManager.mapTasks.containsKey(i)) {
                    Task task = newFileBackedTasksManager.mapTasks.get(i);
                    newFileBackedTasksManager.historyManager.add(task);
                }
            }
            int maxId = 0;
            for (int i : newFileBackedTasksManager.mapTasks.keySet()) {
                if (i > maxId) {
                    maxId = i;
                }
            }
            idInc = maxId + 1;

        } catch (FileNotFoundException exception) {
            exception.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
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
        if (line.length() > 0) line.deleteCharAt(line.length() - 1);
        return line.toString();
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        if (value.isEmpty() || value.isBlank()) {
            System.out.println("Передана пустая строка");
            return Collections.emptyList();
        } else {
            if (value.contains("TASK") || value.contains("EPIC")) {
                return Collections.emptyList();
            }
        }
        String[] line = value.split(",");
        for (String str : line) {
            if (!str.isEmpty() && !str.isBlank()) {
                history.add(Integer.parseInt(str));

            }
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
    public List<Task> getHistory() {
        return super.getHistory();
    }

    private void save() {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(fileHeader + "\n");
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
            throw new ManagerSaveException(e.getMessage());
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
        Optional<Task> task = super.getTaskId(id);
        task.ifPresent(t -> save());
        return task;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void updateTask(int idTask, Task task) {
        super.updateTask(idTask, task);
        save();
    }

    @Override
    public void deleteTaskById(int idTask) {
        super.deleteTaskById(idTask);
        save();
    }

    @Override
    public HashMap<Integer, Task> getListSubtaskEpic(int idTask) {
        return super.getListSubtaskEpic(idTask);
    }

}

