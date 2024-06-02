public class Subtask extends Task {
    private int epicId;
    private String name;
    private String description;
    private final int id;
    private ProgressStage progressStage;

    public Subtask(String name, String description, int id, int epicId, ProgressStage progressStage) {
        super(name, description, id, progressStage);
        this.name = name;
        this.description = description;
        this.id = id;
        this.epicId = epicId;
        this.progressStage = progressStage;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "{" + epicId + ", " + name + ", " + description + ", id=" + id + ", " + progressStage + "}";
    }
}
