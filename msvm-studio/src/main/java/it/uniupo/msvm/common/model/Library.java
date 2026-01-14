package it.uniupo.msvm.common.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Entity che rappresenta una libreria nel Database.
 * Mappa la tabella 'libraries'.
 */
public class Library implements Serializable {

    private String name;        // Primary Key
    private String content;     // Codice Assembly
    private String description;
    private int version;
    public Library() {}
    public Library(String name, String content, String description, int version) {
        this.name = name;
        this.content = content;
        this.description = description;
        this.version = version;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    @Override
    public String toString() {
        return "Library{name='" + name + "', version=" + version + "}";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Library library = (Library) o;
        return Objects.equals(name, library.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}