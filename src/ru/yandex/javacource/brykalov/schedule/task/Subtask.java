package ru.yandex.javacource.brykalov.schedule.task;

import java.time.Duration;
import java.time.LocalDateTime;

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

    public Subtask(String name, String description, int epicId, Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
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
        if (startTime != null && duration != null) {
            return "{" + name + ", " + description + ", id=" + id + ", epicId=" + epicId + ", " + status + ", start/end/duration=" + startTime + "/" + getEndTime().get() + "/" + duration + "}";
        }
        return "{" + name + ", " + description + ", id=" + id + ", epicId=" + epicId + ", " + status + "}";
    }
}
