package ru.sadv1r.sjdbl.entity.model;

import lombok.Data;
import ru.sadv1r.sjdbl.entity.annotation.*;

@Data
@BusinessComponent(blankQueryAllowed = true)
public class Contact {
    @Key
    private String id;

    private String loginName;
    private String firstName;
    private String lastName;

    @SiebelField("M/F")
    private Gender gender;
    @SiebelField("M/M")
    private Mm mm;

    public enum Gender {
        EMPTY(""),
        MALE("M"),
        FEMALE("F");

        @SiebelFieldValue
        String code;

        Gender(String code) {
            this.code = code;
        }
    }

    public enum Mm {
        EMPTY(""),
        MR("Mr."),
        MS("Ms."),
        DR("Dr."),
        MISS("Miss");

        @SiebelFieldValue
        public String code;

        Mm(String code) {
            this.code = code;
        }
    }
}