package task;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> idSubtask;
    private int counterNew;
    private int counterDone;
    private int counterInProgress;

    public Epic(String title, String description, Status status) {
        super(title, description, status);
        setTypeTask(TypeTask.EPIC);
        this.idSubtask = new ArrayList<>();
        this.counterNew = 0;
        this.counterDone = 0;
        this.counterInProgress = 0;
    }

    public Epic() {
        this.idSubtask = new ArrayList<>();
        this.counterNew = 0;
        this.counterDone = 0;
        this.counterInProgress = 0;
    }

    public ArrayList<Integer> getIdSubtask() {
        return idSubtask;
    }


    @Override
    public String toString() {
        return super.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(idSubtask, epic.idSubtask);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), idSubtask);
    }

    public int getCounterNew() {
        return counterNew;
    }

    public int getCounterDone() {
        return counterDone;
    }

    public int getCounterInProgress() {
        return counterInProgress;
    }

    public void setCounterNew(int counterNew) {
        this.counterNew = counterNew;
    }

    public void setCounterDone(int counterDone) {
        this.counterDone = counterDone;
    }

    public void setCounterInProgress(int counterInProgress) {
        this.counterInProgress = counterInProgress;
    }
}
