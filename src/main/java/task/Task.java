package task;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Objects;

public class Task  {
    private TypeTask typeTask;
    private String title;
    private String description;
    private int idTask;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;


    public Task() {
    }

    public Task(String title, String description, Status status, Duration duration, LocalDateTime startTime) {
        this.typeTask = TypeTask.TASK;
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;

    }

    public Task(String title, String description, Status status) {
        this.typeTask = TypeTask.TASK;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(String title, String description) {
        this.typeTask = TypeTask.TASK;
        this.title = title;
        this.description = description;
    }

    public Task(String title, String description, Status status, Duration duration) {
        this.typeTask = TypeTask.TASK;
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = duration;

    }

    public LocalDateTime getEndTime() {
        if (startTime != null) {
            endTime = startTime.plus(duration);
            return endTime;
        } else {
            return endTime = null;
        }
    }

    @Override
    public String toString() {
        return "Тип задачи = " + typeTask +
                ", название = " + title +
                ", описание = " + description +
                ", ID = " + idTask +
                ", статус= " + status +
                ", продолжительность = " + duration +
                ", время начала = " + startTime +
                ", время окончания = " + endTime + "\n"
                ;
    }


    public TypeTask getTypeTask() {
        return typeTask;
    }

    public void setTypeTask(TypeTask typeTask) {
        this.typeTask = typeTask;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIdTask() {
        return idTask;
    }

    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return idTask == task.idTask && typeTask == task.typeTask && Objects.equals(title, task.title)
                && Objects.equals(description, task.description) && status == task.status
                && Objects.equals(duration, task.duration) && Objects.equals(startTime, task.startTime)
                && Objects.equals(endTime, task.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeTask, title, description, idTask, status, duration, startTime, endTime);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}



