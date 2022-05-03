import java.util.Objects;

public class Task {

    private String title;
    private String description;
    private int idTask;
    private String status;

    public Task(int idTask, String title, String description, String status) {
        this.title = title;
        this.description = description;
        this.idTask = idTask;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return  "Название='" + title + '\'' +
                ", описание='" + description + '\'' +
                ", id=" + idTask +
                ", статус='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return idTask == task.idTask && Objects.equals(title, task.title)
                && Objects.equals(description, task.description) && Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, idTask, status);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

