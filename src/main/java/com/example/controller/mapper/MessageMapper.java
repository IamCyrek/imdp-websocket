package com.example.controller.mapper;

import com.example.controller.dto.MessageDTO;
import com.example.controller.dto.UserDTO;
import com.example.model.Message;
import org.mapstruct.*;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "userName", constant = "")
    @Mapping(source = "userDTO.name", target = "content")
    @Mapping(source = "userDTO.creationTime", target = "creationTime")
    @Mapping(source = "room", target = "room")
    Message userDtoToMessage(UserDTO userDTO, Long room);


    @Named("messageToMessageDTO")
    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "creationTime", target = "creationTime")
    MessageDTO messageToMessageDTO(Message message);

    @BeforeMapping
    default void flushMessageDTO(MessageDTO messageDTO) {
        messageDTO.setUserName(HtmlUtils.htmlEscape(messageDTO.getUserName()));
        messageDTO.setContent(HtmlUtils.htmlEscape(messageDTO.getContent()));
    }

    @Mapping(source = "messageDTO.userName", target = "userName")
    @Mapping(source = "messageDTO.content", target = "content")
    @Mapping(source = "messageDTO.creationTime", target = "creationTime")
    @Mapping(source = "room", target = "room")
    Message messageDtoToMessage(MessageDTO messageDTO, Long room);

    @IterableMapping(qualifiedByName = "messageToMessageDTO")
    List<MessageDTO> messageToMessageDTO(List<Message> message);

}
