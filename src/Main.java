public class Main {

    public static void main(String[] args) {
       TaskManager taskManager= new TaskManager();
       taskManager.createEpic(new Epic(TaskManager.getIdInc(), "Переезд","В другой город",
               "NEW"));
       taskManager.createTask(new Task(TaskManager.getIdInc(),"Поход в магазин",
               "Купить продукты на неделю", "NEW"));
        taskManager.createTask(new Task(TaskManager.getIdInc(),"Прогулка",
                "Погулять с собакой ", "NEW"));
       taskManager.createSubtask(new Subtask(TaskManager.getIdInc(),"Найти фирму","Перевозка вещей",
               "NEW",0));
       taskManager.createSubtask(new Subtask(TaskManager.getIdInc(),"Упаковка","Запаковать вещи по коробкам",
                "NEW",0));
       taskManager.createEpic(new Epic(TaskManager.getIdInc(), "Врач","Стоматолог",
                "NEW"));
       taskManager.createSubtask(new Subtask(TaskManager.getIdInc(),"Записаться на прием",
               "Позвонить во торник после 9.00","NEW",5));
       System.out.println(taskManager.getListEpic());
       System.out.println(taskManager.getListTask());
       System.out.println(taskManager.getListSubtask());
       taskManager.updateTask(2,new Task(TaskManager.getIdInc(),"Прогулка",
        "Погулять с собакой ", "DONE"));
        System.out.println(taskManager.getListTask());
        taskManager.updateSubtask(4,new Subtask(TaskManager.getIdInc(),"Упаковка","Запаковать вещи по коробкам",
               "DONE",0));
        taskManager.deleteSubtaskId(3);

        System.out.println(taskManager.getListSubtaskEpic(0));
        System.out.println(taskManager.getListEpic());
        taskManager.updateEpic(0,new Epic(TaskManager.getIdInc(), "Переезд","В другой город",
                "DONE"));


        System.out.println(taskManager.getListEpic());










    }
}
