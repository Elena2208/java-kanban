import task.*;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        manager.createEpic(new Epic(manager.getIdInc(), "Переезд", "В другой город",
                Status.NEW));
        manager.createTask(new Task(manager.getIdInc(), "Поход в магазин",
                "Купить продукты на неделю", Status.NEW));
        manager.createTask(new Task(manager.getIdInc(), "Прогулка",
                "Погулять с собакой ", Status.NEW));
        manager.createSubtask(new Subtask(manager.getIdInc(), "Найти фирму", "Перевозка вещей",
                Status.NEW, 0));
        manager.createSubtask(new Subtask(manager.getIdInc(), "Упаковка", "Запаковать вещи по коробкам",
                Status.NEW, 0));
        manager.createEpic(new Epic(manager.getIdInc(), "Врач", "Стоматолог",
                Status.NEW));
        manager.createSubtask(new Subtask(manager.getIdInc(), "Записаться на прием",
                "Позвонить во торник после 9.00", Status.NEW, 5));

        manager.getTaskId(2);
        manager.getEpiId(5);
        manager.getSubtaskId(3);
        System.out.println(manager.getHistory());
        manager.getEpiId(0);

        manager.getTaskId(2);
        manager.getEpiId(5);
        manager.getSubtaskId(3);
        System.out.println(manager.getHistory());

    }
}
