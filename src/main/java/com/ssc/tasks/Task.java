package com.ssc.tasks;

public class Task {

    private Long id;
    private String title;
    private boolean done = false;
    private boolean favorite = false;

    public Task() {
    }

    public Task(Long id, String title, boolean done) {
        this.id = id;
        this.title = title;
        this.done = done;
    }

    public Task(Long id, String title, boolean done, boolean favorite) {
        this.id = id;
        this.title = title;
        this.done = done;
        this.favorite = favorite;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
