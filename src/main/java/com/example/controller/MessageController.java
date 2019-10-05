package com.example.controller;

import com.example.controller.dto.MessageDTO;
import com.example.controller.mapper.MessageMapper;
import com.example.controller.dto.UserDTO;
import com.example.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import javax.validation.Valid;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    private final MessageMapper messageMapper;

    @MessageMapping("/hello")
    @SendTo("/topic/messages")
    public MessageDTO hello(@Valid UserDTO userDTO,
                            SimpMessageHeaderAccessor headerAccessor) {
        Objects.requireNonNull(headerAccessor.getSessionAttributes())
                .put("userName", HtmlUtils.htmlEscape(userDTO.getName()));

        return messageMapper.messageToMessageDTO(
                messageService.hello(
                        messageMapper.userDtoToMessage(userDTO)));
    }

    @MessageMapping("/messaging")
    @SendTo("/topic/messages")
    public MessageDTO messaging(@Valid MessageDTO messageDTO) {
        return messageMapper.messageToMessageDTO(
                messageService.save(
                        messageMapper.messageDtoToMessage(messageDTO)));
    }

}
