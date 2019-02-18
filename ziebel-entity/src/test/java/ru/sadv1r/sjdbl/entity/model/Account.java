package ru.sadv1r.sjdbl.entity.model;

import lombok.Data;
import ru.sadv1r.sjdbl.entity.ListOfValues;
import ru.sadv1r.sjdbl.entity.annotation.*;

import javax.persistence.FetchType;
import java.util.List;

/**
 * @author sadv1r
 */
@Data
public class Account implements AccountBusObj {
    @Key
    private String id;
    private String name;
    private String accountStatus;
    @MvgField(value = "Street Address", clazz = StreetAddress.class, fetch = FetchType.EAGER)
    private List<StreetAddress> streetAddress;
    @ChildBusCompField(value = "Contact", clazz = Contact.class, fetch = FetchType.EAGER)
    private List<Contact> contacts;
    @LOV("ACCOUNT_TYPE")
    private ListOfValues type;
}
