package com.example.controller;

import com.example.controller.dto.MessageDTO;
import com.example.controller.dto.TypingDTO;
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
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    private final MessageMapper messageMapper;

    @MessageMapping("/hello")
    @SendTo("/topic/hello")
    public List<MessageDTO> hello(@Valid UserDTO userDTO,
                            SimpMessageHeaderAccessor headerAccessor) {
        Objects.requireNonNull(headerAccessor.getSessionAttributes())
                .put("userName", HtmlUtils.htmlEscape(userDTO.getName()));

        messageService.hello(messageMapper.userDtoToMessage(userDTO));

        return messageMapper.messageToMessageDTO(
                messageService.find10LastMessages());
    }

    @MessageMapping("/messaging")
    @SendTo("/topic/messages")
    public MessageDTO messaging(@Valid MessageDTO messageDTO) {
        return messageMapper.messageToMessageDTO(
                messageService.save(
                        messageMapper.messageDtoToMessage(messageDTO)));
    }

    @MessageMapping("/typing")
    @SendTo("/topic/typing")
    public Set<String> typing(@Valid TypingDTO typingDTO) {
        return messageService.typing(typingDTO.getUserName(), typingDTO.getIsTyping());
    }
}
