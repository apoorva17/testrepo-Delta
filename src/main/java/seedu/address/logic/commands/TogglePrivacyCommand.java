package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;
import java.util.Set;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Remark;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.tag.Tag;
import seedu.address.model.team.TeamName;


public class TogglePrivacyCommand extends UndoableCommand {
    public static final String COMMAND_WORD = "toggleprivacy";
    public static final String COMMAND_ALIAS = "tp";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Changes the field privacy of the person"
            + " identified by the index number used in the last person listing.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_PHONE + "]"
            + "[" + PREFIX_EMAIL + "]"
            + "[" + PREFIX_ADDRESS + "]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + " "
            + PREFIX_EMAIL + " "
            + PREFIX_ADDRESS;

    public static final String MESSAGE__SUCCESS = "Changed the Privacy of the Person: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    private final Index index;
    private final EditPersonPrivacy epp;

    private Person personToEdit;
    private Person editedPerson;

    /**
     * @param index of the person in the filtered person list to edit privacy
     */
    public TogglePrivacyCommand(Index index, EditPersonPrivacy epp) {
        requireNonNull(index);
        requireNonNull(epp);

        this.index = index;
        this.epp = epp;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        personToEdit = lastShownList.get(index.getZeroBased());
        try {
            editedPerson = createEditedPrivacyPerson(personToEdit, epp);
        } catch (IllegalValueException e) {
            throw new AssertionError("Person must have all fields!");
        }

        try {
            model.updatePerson(personToEdit, editedPerson);
        } catch (DuplicatePersonException dpe) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError("The target person cannot be missing");
        }
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE__SUCCESS, editedPerson));
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     * NOTE: Private fields will not be edited.
     */
    private static Person createEditedPrivacyPerson(Person personToEdit, EditPersonPrivacy epp) throws IllegalValueException {
        assert personToEdit != null;

        Name updatedName = personToEdit.getName();
        Phone updatedPhone = createPhonePrivacy(personToEdit, epp);
        Email updatedEmail = createEmailPrivacy(personToEdit, epp);
        Address updatedAddress = createAddressPrivacy(personToEdit, epp);
        Remark updatedRemark = createRemarkPrivacy(personToEdit, epp);
        TeamName updatedTeamName = personToEdit.getTeamName();
        Set<Tag> updatedTags = personToEdit.getTags();

        return new Person(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedRemark,
                updatedTeamName, updatedTags);
    }

    private static Phone createPhonePrivacy(Person person, EditPersonPrivacy epp) {
        Phone phone;
        try {
            if (person.getPhone().isPrivate()) {
                person.getPhone().togglePrivacy();
                phone = new Phone(person.getPhone().toString());
                person.getPhone().togglePrivacy();
            } else {
                phone = new Phone(person.getPhone().toString());
            }
        } catch (Exception e) {
            throw new AssertionError("Invalid Phone");
        }
        if (epp.getPrivatePhone() != null) {
            phone.setPrivate(epp.getPrivatePhone());
        } else {
            phone.setPrivate(person.getPhone().isPrivate());
        }

        return phone;
    }

    private static Email createEmailPrivacy(Person person, EditPersonPrivacy epp) {
        Email email;
        try {
            if (person.getEmail().isPrivate()) {
                person.getEmail().togglePrivacy();
                email = new Email(person.getEmail().toString());
                person.getEmail().togglePrivacy();
            } else {
                email = new Email(person.getEmail().toString());
            }
        } catch (Exception e) {
            throw new AssertionError("Invalid Email");
        }
        if (epp.getPrivateEmail() != null) {
            email.setPrivate(epp.getPrivateEmail());
        } else {
            email.setPrivate(person.getEmail().isPrivate());
        }
        return email;
    }

    private static Address createAddressPrivacy(Person person, EditPersonPrivacy epp) {
        Address address;
        try {
            if (person.getAddress().isPrivate()) {
                person.getAddress().togglePrivacy();
                address = new Address(person.getAddress().toString());
                person.getAddress().togglePrivacy();
            } else {
                address = new Address(person.getAddress().toString());
            }
        } catch (Exception e) {
            throw new AssertionError("Invalid Address");
        }
        if (epp.getPrivateAddress() != null) {
            address.setPrivate(epp.getPrivateAddress());
        } else {
            address.setPrivate(person.getAddress().isPrivate());
        }
        return address;
    }

    private static Remark createRemarkPrivacy(Person person, EditPersonPrivacy epp) {
        Remark remark;
        try {
            if (person.getRemark().isPrivate()) {
                person.getRemark().togglePrivacy();
                remark = new Remark(person.getRemark().toString());
                person.getRemark().togglePrivacy();
            } else {
                remark = new Remark(person.getRemark().toString());
            }
        } catch (Exception e) {
            throw new AssertionError("Invalid Remark");
        }
        if (epp.getPrivateRemark() != null) {
            remark.setPrivate(epp.getPrivateRemark());
        } else {
            remark.setPrivate(person.getRemark().isPrivate());
        }
        return remark;
    }

    public Index getIndex() {
        return index;
    }

    public EditPersonPrivacy getEpp() {
        return epp;
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof TogglePrivacyCommand)) {
            return false;
        }

        // state check
        TogglePrivacyCommand e = (TogglePrivacyCommand) other;
        return index.equals(e.index)
                && epp.equals(e.epp);
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonPrivacy {
        private Boolean privatePhone;
        private Boolean privateEmail;
        private Boolean privateAddress;
        private Boolean privateRemark;

        public EditPersonPrivacy() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonPrivacy (EditPersonPrivacy toCopy) {
            this.privateAddress = toCopy.privateAddress;
            this.privateEmail = toCopy.privateEmail;
            this.privatePhone = toCopy.privatePhone;
            this.privateRemark = toCopy.privateRemark;
        }

        public void setPrivatePhone(boolean privatePhone) {
            this.privatePhone = privatePhone;
        }
        public void togglePhone() {
            this.privatePhone = (privatePhone) ? false : true;
        }

        public Boolean getPrivatePhone() {
            return privatePhone;
        }

        public void setPrivateEmail(boolean privateEmail) {
            this.privateEmail = privateEmail;
        }

        public void toggleEmail() {
            this.privateEmail = (privateEmail) ? false : true;
        }

        public Boolean getPrivateEmail() {
            return privateEmail;
        }

        public void setPrivateAddress(boolean privateAddress) {
            this.privateAddress = privateAddress;
        }

        public void toggleAddress() {
            this.privateAddress = (privateAddress) ? false : true;
        }

        public Boolean getPrivateAddress() {
            return privateAddress;
        }

        public void setPrivateRemark(boolean privateRemark) {
            this.privateRemark = privateRemark;
        }

        public void toggleRemark() {
            this.privateRemark = (privateRemark) ? false : true;
        }

        public Boolean getPrivateRemark() {
            return privateRemark;
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonPrivacy)) {
                return false;
            }

            // state check
            EditPersonPrivacy e = (EditPersonPrivacy) other;

            return getPrivatePhone().equals(e.getPrivatePhone())
                    && getPrivateAddress().equals(e.getPrivateAddress())
                    && getPrivateEmail().equals(e.getPrivateEmail())
                    && getPrivateRemark().equals(e.getPrivateRemark());
        }
    }
}
