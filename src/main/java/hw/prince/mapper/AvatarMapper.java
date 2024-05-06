package hw.prince.mapper;

import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import hw.prince.controller.AvatarController;
import hw.prince.dto.AvatarDto;
import hw.prince.model.Avatar;

@Component
public class AvatarMapper {

    private int port;

    public AvatarDto toDto(Avatar avatar) {
        AvatarDto avatarDto = new AvatarDto();
        avatarDto.setId(avatar.getId());
        avatarDto.setStudentName(avatar.getStudent().getName());
        avatarDto.setUrl(
                UriComponentsBuilder.newInstance()
                        .scheme("http")
                        .host("localhost")
                        .port(port)
                        .path(AvatarController.BASE_URI)
                        .pathSegment(("from-db"))
                        .queryParam("studentId", avatar.getStudent().getId())
                        .build()
                        .toString()
        );
        return avatarDto;
    }
}
