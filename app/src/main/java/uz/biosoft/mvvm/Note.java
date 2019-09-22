package uz.biosoft.mvvm;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Note {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private int prioraty;

    public Note(String title, String description, int prioraty) {
        this.title = title;
        this.description = description;
        this.prioraty = prioraty;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPrioraty() {
        return prioraty;
    }
}
