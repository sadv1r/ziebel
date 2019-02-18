package ru.sadv1r.sjdbl.entity;

import org.junit.*;
import ru.sadv1r.sjdbl.core.exception.MoreThanOneRecordFoundExceptions;
import ru.sadv1r.sjdbl.core.exception.RecordNotFoundException;
import ru.sadv1r.sjdbl.core.exception.SjdblException;
import ru.sadv1r.sjdbl.entity.model.Account;
import ru.sadv1r.sjdbl.entity.model.Contact;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class SessionIT {
    private static SessionFactory sessionFactory;
    private Session session;

    @BeforeClass
    public static void start() throws Exception {
        final Configuration configuration = new Configuration("sjdbl.properties");
        sessionFactory = configuration.buildSessionFactory();
    }

    @AfterClass
    public static void stop() {
        sessionFactory.close();
    }

    @Before
    public void setUp() throws Exception {
        session = sessionFactory.openSession();
    }

    @After
    public void tearDown() throws Exception {
        if (session.isOpen()) {
            session.close();
        }
    }

    @Test
    public void getByKey() throws SjdblException {
        session = sessionFactory.openSession();

        Optional<Account> optionalAccount = session.get(Account.class, "1-38P");

        assertThat(optionalAccount).isPresent().get().hasFieldOrPropertyWithValue("name", "test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void blankQueryTest() throws SjdblException {
        session = sessionFactory.openSession();

        session.get(new Account());
    }

    @Test
    public void checkLov() throws SjdblException {
        session = sessionFactory.openSession();

        Optional<Account> optionalAccount = session.get(Account.class, "1-38P");

        assertThat(optionalAccount).isPresent().get().hasFieldOrPropertyWithValue("type.value", "Clinic");
    }

    @Test
    public void getByKeyWrong() throws SjdblException {
        session = sessionFactory.openSession();

        Optional<Account> optionalAccount = session.get(Account.class, "1-11111");

        assertThat(optionalAccount).isNotPresent();
    }

    @Test
    public void loadByKey() throws SjdblException {
        session = sessionFactory.openSession();

        Account optionalUser = session.load(Account.class, "1-38P");

        assertThat(optionalUser).isNotNull().hasFieldOrPropertyWithValue("name", "test");
    }

    @Test(expected = RecordNotFoundException.class)
    public void loadByKeyWrong() throws SjdblException {
        session = sessionFactory.openSession();

        session.load(Account.class, "1-11111");
    }

    @Test
    public void getByObject() throws SjdblException {
        session = sessionFactory.openSession();

        Account account = new Account();
        account.setId("1-38P");
        session.get(account);

        assertThat(account.getName()).isEqualTo("test");
    }

    @Test(expected = MoreThanOneRecordFoundExceptions.class)
    public void getByObjectWrong() throws SjdblException {
        session = sessionFactory.openSession();

        Contact user = new Contact();
        user.setFirstName("Guest");
        session.get(user);
    }

    @Test
    public void list() throws SjdblException {
        Contact user = new Contact();
        user.setFirstName("Guest");
        List<Contact> users = session.list(user);

        assertThat(users).extracting(Contact::getId, Contact::getLastName).containsExactly(
                tuple("0-3FTZJ", "Channel Partner"),
                tuple("0-3FTZ9", "Customer"));
    }

    @Test
    public void checkEnum() throws SjdblException {
        session = sessionFactory.openSession();

        Contact contact = new Contact();
        contact.setFirstName("Siebel OR [First Name] = Guest");
        List<Contact> contacts = session.list(contact);

        assertThat(contacts).extracting(Contact::getMm, Contact::getLastName).containsExactly(
                tuple(Contact.Mm.EMPTY, "Administrator"),
                tuple(Contact.Mm.MR, "Channel Partner"),
                tuple(Contact.Mm.MS, "Customer"));
    }

    @Test
    public void save() throws Exception {
        session = sessionFactory.openSession();

        final Contact contact = new Contact();
        final String contactFieldsIdentifier = String.valueOf(UUID.randomUUID());
        contact.setFirstName("First Name ".concat(contactFieldsIdentifier));
        contact.setLastName("Last Name ".concat(contactFieldsIdentifier));
        contact.setMm(Contact.Mm.MR);

        assertThat(session.save(contact)).isNotNull().isNotEmpty();
    }
}