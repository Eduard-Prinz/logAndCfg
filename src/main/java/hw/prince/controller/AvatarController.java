package hw.prince.controller;


import jakarta.validation.constraints.Min;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import hw.prince.dto.AvatarDto;
import hw.prince.service.AvatarService;

import java.util.List;

@RestController
@RequestMapping(AvatarController.BASE_URI)
@Validated
public class AvatarController {

    public final static String BASE_URI = "/avatars";

    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadAvatar(@RequestParam long studentId, @RequestPart MultipartFile avatar) {
        avatarService.uploadAvatar(studentId, avatar);
    }

    @GetMapping("/from-db")
    public ResponseEntity<byte[]> getAvatarFromDb(@RequestParam long studentId) {
        return transform(avatarService.getAvatarFromDb(studentId));
    }

    @GetMapping("/from-fs")
    public ResponseEntity<byte[]> getAvatarFromFs(@RequestParam long studentId) {
        return transform(avatarService.getAvatarFromFs(studentId));
    }


    @GetMapping
    public List<AvatarDto> getAvatars(@RequestParam @Min(value = 1, message = "Minimum page number = 1") int page,
                                      @RequestParam @Min(value = 1, message = "Minimum page size = 1") int size) {
        return avatarService.getAvatars(page, size);
    }


    private ResponseEntity<byte[]> transform(Pair<byte[], String> pair) {
        byte[] data = pair.getFirst();
        return ResponseEntity.status(HttpStatus.OK)
                .contentLength(data.length)
                .contentType(MediaType.parseMediaType(pair.getSecond()))
                .body(data);
    }
}
