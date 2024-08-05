package ru.yandex.javacource.brykalov.schedule.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Task {
    protected TaskType type;
    protected String name;
    protected String description;
    protected int id;
    protected Status status;
    protected LocalDateTime startTime;
    protected Duration duration;

    public Task(String name, String description, Status status) {
        this.type = TaskType.TASK;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.type = TaskType.TASK;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public TaskType getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
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

    public Optional<LocalDateTime> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Optional<Duration> getDuration() {
        return Optional.ofNullable(duration);
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Optional<LocalDateTime> getEndTime() {
        if (startTime != null && duration != null) {
            return Optional.of(startTime.plus(duration));
        }
        return Optional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    @Override
    public String toString() {
        if (startTime != null && duration != null && getEndTime().isPresent()) {
            return "{" + name + ", " + description + ", id=" + id + ", " + status + ", start/end/duration=" + startTime + "/" + getEndTime().get() + "/" + duration + "}";
        }
        return "{" + name + ", " + description + ", id=" + id + ", " + status + "}";
    }
}
