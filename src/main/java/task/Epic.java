package task;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import java.util.Objects;


public class Epic extends Task {
    private ArrayList<Integer> idSubtask;
    private int counterNew;
    private int counterDone;
    private int counterInProgress;

    public Epic(String title, String description) {
        super(title,description);
        setTypeTask(TypeTask.EPIC);
        setStatus(Status.NEW);
        this.idSubtask = new ArrayList<>();
        this.counterNew = 0;
        this.counterDone = 0;
        this.counterInProgress = 0;
    }

    public Epic() {
        setStatus(Status.NEW);
        this.idSubtask = new ArrayList<>();
        this.counterNew = 0;
        this.counterDone = 0;
        this.counterInProgress = 0;
    }

    public ArrayList<Integer> getIdSubtask() {
        return idSubtask;
    }

    public int getCounterNew() {
        return counterNew;
    }

    public void setCounterNew(int counterNew) {
        this.counterNew = counterNew;
    }

    public int getCounterDone() {
        return counterDone;
    }

    public void setCounterDone(int counterDone) {
        this.counterDone = counterDone;
    }

    public int getCounterInProgress() {
        return counterInProgress;
    }

    public void setCounterInProgress(int counterInProgress) {
        this.counterInProgress = counterInProgress;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return counterNew == epic.counterNew && counterDone == epic.counterDone
                && counterInProgress == epic.counterInProgress && Objects.equals(idSubtask, epic.idSubtask);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), idSubtask, counterNew, counterDone, counterInProgress);
    }
}

