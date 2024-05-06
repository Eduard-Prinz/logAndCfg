package hw.prince.exception;

public class FacultyNotFoundException extends NotFoundException {
    private final long id;
    public FacultyNotFoundException(long id) {
        this.id = id;
    }
    @Override
    public String getMessage() {
        return "Faculty with id: %d not found!".formatted(id);
    }
}
