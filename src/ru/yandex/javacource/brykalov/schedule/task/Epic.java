package ru.yandex.javacource.brykalov.schedule.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.type = TaskType.EPIC;
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public void addSubtaskId(Integer subtaskId) {
        if (subtaskIds.contains(subtaskId)) {
            return;
        }
        subtaskIds.add(subtaskId);
    }

    public void removeSubtask(Integer subtaskId) {
        subtaskIds.remove(subtaskId);
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(List<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        if (startTime != null && duration != null && getEndTime().isPresent()) {
            return "{" + name + ", " + description + ", id=" + id + ", SubtasksId=" + subtaskIds + ", " + status
                    + ", start/end/duration=" + startTime + "/" + getEndTime().get() + "/" + duration + "}";
        }
        return "{" + name + ", " + description + ", id=" + id + ", SubtasksId=" + subtaskIds + ", " + status + "}";
    }
}
