package hw.prince.exception;

public class AvatarNotFoundException extends NotFoundException {
    private final long studentId;
    public AvatarNotFoundException(long id) {
        this.studentId = id;
    }
    @Override
    public String getMessage() {
        return "Avatar for student with id: %d not found!".formatted(studentId);
    }

}
