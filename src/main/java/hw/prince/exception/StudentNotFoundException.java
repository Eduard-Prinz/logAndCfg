package hw.prince.exception;

public class StudentNotFoundException extends NotFoundException {

    private final long id;

    public StudentNotFoundException(long id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Student with id: %d not found!".formatted(id);
    }
}
