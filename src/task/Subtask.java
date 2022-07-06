package task;

import java.util.Objects;

public class Subtask extends Task {
    private int idEpic;

    public Subtask() {
    }

    public Subtask(String title, String description, Status status, int idEpic) {
        super(title, description, status);
        setTypeTask(TypeTask.SUBTASK);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    public void setIdEpic(int idEpic) {
        this.idEpic = idEpic;
    }

    @Override
    public String toString() {
        return super.toString() + ", эпик № = " + idEpic;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return idEpic == subtask.idEpic;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), idEpic);
    }
}
