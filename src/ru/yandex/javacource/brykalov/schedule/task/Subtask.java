package ru.yandex.javacource.brykalov.schedule.task;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId, Status status) {
        super(name, description, status);
        this.type = TaskType.SUBTASK;
        this.name = name;
        this.description = description;
        this.epicId = epicId;
        this.status = status;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "{" + name + ", " + description + ", id=" + id + ", epicId=" + epicId + ", " + status + "}";
    }
}
