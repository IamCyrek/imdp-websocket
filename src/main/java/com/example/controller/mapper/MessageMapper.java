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
    @Mapping(source = "name", target = "content")
    @Mapping(source = "creationTime", target = "creationTime")
    Message userDtoToMessage(UserDTO userDTO);


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

    @Named("messageDtoToMessage")
    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "creationTime", target = "creationTime")
    Message messageDtoToMessage(MessageDTO messageDTO);

    @IterableMapping(qualifiedByName = "messageToMessageDTO")
    List<MessageDTO> messageToMessageDTO(List<Message> message);

    @IterableMapping(qualifiedByName = "messageDtoToMessage")
    List<Message> messageDtoToMessage(List<MessageDTO> messageDTO);

}
