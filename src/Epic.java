import java.util.HashMap;

public class Epic extends Task {
    private String name;
    private String description;
    private final int id;
    private ProgressStage progressStage;
    private HashMap <Integer, Subtask> subtaskHashMap = new HashMap<>();

    public Epic(String name, String description, int id) {
        super(name, description, id, ProgressStage.NEW);
        this.name = name;
        this.description = description;
        this.id = id;
        this.progressStage = ProgressStage.NEW;
    }

    public Epic(String name, String description, int id, ProgressStage progressStage) {
        super(name, description, id, progressStage);
        this.name = name;
        this.description = description;
        this.id = id;
        this.progressStage = progressStage;
    }

    public void updateSubtaskHashMap(Subtask subtask) {
        if (subtaskHashMap.containsKey(subtask.getId())) {
            subtaskHashMap.replace(subtask.getId(), subtask);
        } else {
            subtaskHashMap.put(subtask.getId(), subtask);
        }
    }

    public void removeSubtask(int subtaskId) {
        subtaskHashMap.remove(subtaskId);
    }

    public void replaceSubtask(int subtaskId, Subtask subtask) {
        subtaskHashMap.replace(subtaskId, subtask);
    }

    public HashMap<Integer, Subtask> getSubtaskHashMap() {
        return subtaskHashMap;
    }

    public void setSubtaskHashMap(HashMap<Integer, Subtask> subtaskHashMap) {
        this.subtaskHashMap = subtaskHashMap;
    }

    @Override
    public String toString() {
        return "{" + name + ", " + description + ", id=" + id + ", " + progressStage + ", " + subtaskHashMap + "}";
    }
}
