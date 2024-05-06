package hw.prince.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import hw.prince.dto.AvatarDto;
import hw.prince.model.Avatar;
import hw.prince.model.Student;
import hw.prince.exception.AvatarNotFoundException;
import hw.prince.exception.AvatarProcessingException;
import hw.prince.exception.StudentNotFoundException;
import hw.prince.mapper.AvatarMapper;
import hw.prince.repository.AvatarRepository;
import hw.prince.repository.StudentRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;



@Service
public class AvatarService {

    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;
    private final AvatarMapper avatarMapper;
    private final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    private final Path pathToAvatarsDir;


    public AvatarService(AvatarRepository avatarRepository,
                         StudentRepository studentRepository,
                         AvatarMapper avatarMapper,
                         @Value("${application.path-to-avatars-dir}") String pathToAvatarsDir) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
        this.avatarMapper = avatarMapper;
        this.pathToAvatarsDir = Paths.get(pathToAvatarsDir);
    }

    @PostMapping
    public void init() {
        logger.info("method init was invoked");
        try {
            if (!Files.exists(pathToAvatarsDir) || !Files.isDirectory(pathToAvatarsDir)) {
                Files.createDirectories(pathToAvatarsDir);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void uploadAvatar(long studentId, MultipartFile image) {
        logger.debug("method uploadAvatar was invoked with parameters studentId ={}, image = {}", studentId, image);
        try {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new StudentNotFoundException(studentId));
            byte[] data = image.getBytes();
            String extension = StringUtils.getFilenameExtension(image.getOriginalFilename());
            String fileNmae = String.format("%s.%s", UUID.randomUUID(), extension);
            Path path = pathToAvatarsDir.resolve(fileNmae);
            Files.write(path, data);

            Avatar avatar = new Avatar();
            avatar.setStudent(student);
            avatar.setData(data);
            avatar.setFileSize(data.length);
            avatar.setMediaType(image.getContentType());
            avatar.setFilePath(path.toString());

            avatarRepository.save(avatar);
        } catch (IOException e) {
            throw new AvatarProcessingException(e);
        }
    }

    public Pair<byte[], String> getAvatarFromDb(long studentId) {
        logger.debug("method getAvatarFromDb was invoked with parameter studentId ={},", studentId);
        Avatar avatar = avatarRepository.findByStudent_Id(studentId)
                .orElseThrow(() -> new AvatarNotFoundException(studentId));
        return Pair.of(avatar.getData(), avatar.getMediaType());
    }

    public Pair<byte[], String> getAvatarFromFs(long studentId) {
        logger.debug("method getAvatarFromFs was invoked with parameter studentId ={},", studentId);
        try {
            Avatar avatar = avatarRepository.findByStudent_Id(studentId)
                    .orElseThrow(() -> new AvatarNotFoundException(studentId));
            byte[] data = Files.readAllBytes(Paths.get(avatar.getFilePath()));
            return Pair.of(data, avatar.getMediaType());
        } catch (IOException e) {
            throw new AvatarProcessingException(e);
        }
    }

    public List<AvatarDto> getAvatars(int page, int size) {
        logger.debug("method getAvatars was invoked with parameters page ={}, size = {}", page, size);
        return avatarRepository.findAll(PageRequest.of(page - 1, size)).get()
                .map(avatarMapper::toDto)
                .collect(Collectors.toList());
    }
}