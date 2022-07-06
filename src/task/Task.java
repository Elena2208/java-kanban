package task;

import java.util.Objects;

public class Task {
    private TypeTask typeTask;
    private String title;
    private String description;
    private int idTask;
    private Status status;

    public Task(String title, String description, Status status) {
        this.typeTask = TypeTask.TASK;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task() {
    }


    public TypeTask getTypeTask() {
        return typeTask;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getIdTask() {
        return idTask;
    }

    public Status getStatus() {
        return status;
    }

    public void setTypeTask(TypeTask typeTask) {
        this.typeTask = typeTask;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return idTask == task.idTask && typeTask == task.typeTask && Objects.equals(title, task.title)
                && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeTask, title, description, idTask, status);
    }

    @Override
    public String toString() {
        return "Тип задачи = " + typeTask +
                ", название = " + title +
                ", описание = " + description +
                ", ID = " + idTask +
                ", статус= " + status;
    }
}

