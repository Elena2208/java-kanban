import manager.Managers;
import manager.TaskManager;
import task.*;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        manager.createTask(new Task("Позвонить маме", "Уточнить инфу",Status.NEW));
        manager.createTask(new Task("Записаться к врачу", " На июнь ",Status.IN_PROGRESS));
        manager.createEpic(new Epic("ДР", "Подготовка", Status.NEW));
        manager.createSubtask(new Subtask("Подарок", "Забрать на почте", Status.NEW, 2));
        manager.createSubtask(new Subtask("Ресторан", "Подтвердить ", Status.DONE, 2));
        manager.createSubtask(new Subtask("Приглашения", "Ватсап", Status.IN_PROGRESS,2));
        manager.createEpic(new Epic("Соревнования", "Подготовка", Status.NEW));
        manager.getEpiId(2);
        manager.getEpiId(6);
        manager.getEpiId(2);
        System.out.println(manager.getHistory());
        manager.getSubtaskId(4);
        manager.getSubtaskId(5);
        manager.getSubtaskId(3);
        manager.getTaskId(0);
        manager.getTaskId(1);
        System.out.println(manager.getHistory());
        manager.deleteTaskById(0);
        System.out.println(manager.getHistory());
        manager.deleteEpicById(2);
        System.out.println(manager.getHistory());


    }
}
