import manager.FileBackedTasksManager;
import manager.Managers;
import manager.TaskManager;
import task.*;

import java.io.File;

import static java.lang.System.out;

public class Main {

    public static void main(String[] args) {
        File file = new File("\\Tasks.csv");
        TaskManager manager = Managers.getDefault();
        manager.createTask(new Task("Позвонить маме", "Уточнить инфу", Status.NEW));
        manager.createTask(new Task("Записаться к врачу", " На июнь ", Status.IN_PROGRESS));
        manager.createEpic(new Epic("ДР", "Подготовка", Status.NEW));
        manager.createSubtask(new Subtask("Подарок", "Почта", Status.DONE, 2));
        manager.createSubtask(new Subtask("Приглашения", "Рассылка", Status.DONE, 2));
        manager.createTask(new Task("Заплатить коммуналку", " срочно ", Status.DONE));
        manager.getEpiId(2);
        FileBackedTasksManager newFile = FileBackedTasksManager.loadFromFile(file);

        out.println(manager.getListEpic());
        out.println(manager.getListSubtask());
        out.println(manager.getListTask());
        out.println(manager.getHistory());
        manager.getSubtaskId(4);
        out.println(manager.getHistory());


    }
}
