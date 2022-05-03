import java.util.Objects;

public class Subtask extends Task {
    private int idEpic;

    public Subtask(int idTask, String title, String description, String status, int idEpic) {
        super(idTask, title, description, status);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() {
        return "Входит в эпик № = " + idEpic + ", " +
                super.toString() ;

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
