package manager;

import task.*;

import java.io.*;
import java.nio.file.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static java.lang.System.*;
import static task.TypeTask.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final static String fileHeader = "id,type,name,status,description,duration,start_time,epic";
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
                out.println("Ошибка при попытке удаления существующего файла для пересоздания");
                exception.printStackTrace();
            }
        } else {
            try {
                Files.createFile(Paths.get(fileName));
            } catch (IOException exception) {
                out.println("Ошибка при попытке создания файла");
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
            if (files.isEmpty()) return new FileBackedTasksManager();
            String[] line = files.split("\n");
            int endFile = 0;
            for (int i = 1; i < line.length; i++) {
                if (line[i].isBlank() || line[i].isEmpty()) {
                    endFile = i;
                    break;
                }
                String[] str = line[i].split(",");
                TypeTask taskType = valueOf(str[1]);
                switch (taskType) {
                    case TASK:
                        Task task = newFileBackedTasksManager.taskFromString(line[i]);
                        newFileBackedTasksManager.createTask(task);
                        break;
                    case SUBTASK:
                        Subtask subtask = (Subtask) newFileBackedTasksManager.taskFromString(line[i]);
                        newFileBackedTasksManager.createSubtask(subtask);
                        Epic epic = newFileBackedTasksManager.mapEpic.get(subtask.getIdEpic());
                        epic.getIdSubtask().add(subtask.getIdTask());
                        break;
                    case EPIC:
                        Epic epicNew = (Epic) newFileBackedTasksManager.taskFromString(line[i]);
                        newFileBackedTasksManager.createEpic(epicNew);
                        break;
                    default:
                }
            }
            List<Integer> list = historyFromString(line[endFile + 1]);
            for (int i : list) {
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


            int maxId = 0;
            for (int i : newFileBackedTasksManager.mapTask.keySet()) {
                if (i > maxId) {
                    maxId = i;
                }
            }
            for (int i : newFileBackedTasksManager.mapSubtask.keySet()) {
                if (i > maxId) {
                    maxId = i;
                }
            }
            for (int i : newFileBackedTasksManager.mapEpic.keySet()) {
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
                        task.getTitle(), task.getStatus().toString(), task.getDescription(),
                        task.getDuration().toString(), task.getStartTime().toString());
                break;
            case EPIC:
                Epic epic = (Epic) task;
                line = String.join(",", Integer.toString(epic.getIdTask()), epic.getTypeTask().toString(),
                        epic.getTitle(), epic.getStatus().toString(), epic.getDescription(),
                        epic.getDuration().toString(), epic.getStartTime().toString());
                break;
            case SUBTASK:
                Subtask subtask = (Subtask) task;
                line = String.join(",", Integer.toString(subtask.getIdTask()), subtask.getTypeTask().toString(),
                        subtask.getTitle(), subtask.getStatus().toString(), subtask.getDescription(),
                        subtask.getDuration().toString(), subtask.getStartTime().toString(),
                        Integer.toString(subtask.getIdEpic()));
                break;
            default:
                out.println("Не определен тип задачи");
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
            out.println("История просмотров отсутствует");
            return Collections.emptyList();
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
            out.println("Передана пустая строка");
            return newTask;
        }
        String[] line = value.split(",");
        switch (line[1]) {
            case "TASK":
                newTask = new Task();
                newTask.setIdTask(Integer.parseInt(line[0]));
                newTask.setTypeTask(TASK);
                newTask.setTitle(line[2]);
                newTask.setStatus(Status.valueOf(line[3]));
                newTask.setDescription(line[4]);
                newTask.setDuration(Duration.parse(line[5]));
                newTask.setStartTime(LocalDateTime.parse(line[6]));
                break;
            case "SUBTASK":
                newTask = new Subtask();
                newTask.setIdTask(Integer.parseInt(line[0]));
                newTask.setTypeTask(SUBTASK);
                newTask.setTitle(line[2]);
                newTask.setStatus(Status.valueOf(line[3]));
                newTask.setDescription(line[4]);
                newTask.setDuration(Duration.parse(line[5]));
                newTask.setStartTime(LocalDateTime.parse(line[6]));
                ((Subtask) newTask).setIdEpic(Integer.parseInt(line[7]));
                break;
            case "EPIC":
                newTask = new Epic();
                newTask.setIdTask(Integer.parseInt(line[0]));
                newTask.setTypeTask(EPIC);
                newTask.setTitle(line[2]);
                newTask.setStatus(Status.valueOf(line[3]));
                newTask.setDescription(line[4]);
                newTask.setDuration(Duration.parse(line[5]));
                newTask.setStartTime(LocalDateTime.parse(line[6]));
                break;
            default:
                out.println("Задача не создана");
        }
        return newTask;

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
            if (historyToString(historyManager).isEmpty()) {
                writer.write("  ");
            }
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
    public Optional<Subtask> getSubtaskId(int id) {
        Optional<Subtask> subtask = super.getSubtaskId(id);
        subtask.ifPresent(t -> save());
        return subtask;
    }

    @Override
    public Optional<Epic> getEpicId(int id) {
        Optional<Epic> epic = super.getEpicId(id);
        epic.ifPresent(t -> save());
        return epic;
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        getDurationEpic(epic.getIdTask());
        getStartTimeEpic(epic.getIdTask());
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
        getDurationEpic(epic.getIdTask());
        getStartTimeEpic(epic.getIdTask());
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
        getDurationEpic(idEpic);
        getStartTimeEpic(idEpic);
        save();
    }

}



