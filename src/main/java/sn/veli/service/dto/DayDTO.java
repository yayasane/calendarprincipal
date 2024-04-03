package sn.veli.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link sn.veli.domain.Day} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DayDTO implements Serializable {

    private Long id;

    @NotNull
    private String date;

    @NotNull
    private String dayOfWeek;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DayDTO)) {
            return false;
        }

        DayDTO dayDTO = (DayDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, dayDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DayDTO{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", dayOfWeek='" + getDayOfWeek() + "'" +
            "}";
    }
}
