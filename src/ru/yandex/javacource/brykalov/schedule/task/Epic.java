package ru.yandex.javacource.brykalov.schedule.task;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
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

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    public void setStatus(Status status) { //пришлось сделать публичным, иначе не было доступа для менеджера, который в соседнем пакете
        this.status = status;
    }

    @Override
    public String toString() {
        return "{" + name + ", " + description + ", id=" + id + ", SubtasksId=" + subtaskIds + ", " + status + "}";
    }
}
