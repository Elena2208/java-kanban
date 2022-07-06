import manager.FileBackedTasksManager;
import manager.Managers;
import manager.TaskManager;
import task.*;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        File file = new File("\\Tasks.csv");
        TaskManager manager = Managers.getDefault();
        manager.createTask(new Task("Позвонить маме", "Уточнить инфу", Status.NEW));
        manager.createTask(new Task("Записаться к врачу", " На июнь ", Status.IN_PROGRESS));
        manager.createTask(new Epic("ДР", "Подготовка", Status.NEW));
        manager.createTask(new Subtask("Подарок", "Почта", Status.IN_PROGRESS, 2));
        manager.createTask(new Subtask("Приглашения", "Рассылка", Status.DONE, 2));
        manager.createTask(new Task("Заплатить коммуналку", " срочно ", Status.DONE));
        System.out.println(manager.getListTask());
        manager.updateTask(3, new Subtask("Подарок", "Почта", Status.DONE, 2));
        System.out.println(manager.getListEpic());
        manager.getTaskId(0);
        manager.getTaskId(5);

        FileBackedTasksManager newFile = FileBackedTasksManager.loadFromFile(file);

        manager.createTask(new Subtask("new", "epic", Status.NEW, 2));
        System.out.println(manager.getListEpic());
        System.out.println(manager.getListTask());
        manager.getTaskId(6);
        manager.getTaskId(4);
        System.out.println(manager.getListSubtaskEpic(2));

    }
}
