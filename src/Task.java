public class Task {
    private String name;
    private String description;
    private final int id;
    private ProgressStage progressStage;

    public Task(String name, String description, int id, ProgressStage progressStage) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.progressStage = progressStage;
    }

    public int getId() {
        return id;
    }

    public ProgressStage getProgressStage() {
        return progressStage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProgressStage(ProgressStage progressStage) {
        this.progressStage = progressStage;
    }

    @Override
    public String toString() {
        return "{" + name + ", " + description + ", id=" + id + ", " + progressStage + "}";
    }
}
