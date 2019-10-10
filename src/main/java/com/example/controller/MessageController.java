package com.example.controller;

import com.example.controller.dto.MessageDTO;
import com.example.controller.dto.TypingDTO;
import com.example.controller.mapper.MessageMapper;
import com.example.controller.dto.UserDTO;
import com.example.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessageSendingOperations messagingTemplate;

    private final MessageService messageService;

    private final MessageMapper messageMapper;

    @MessageMapping("/hello/{room}")
    @SendTo("/topic/hello/{room}")
    public List<MessageDTO> hello(@DestinationVariable @Size(max = 9) Long room,
                                  @Valid UserDTO userDTO,
                                  SimpMessageHeaderAccessor headerAccessor) {
        Objects.requireNonNull(headerAccessor.getSessionAttributes())
                .put("userName", HtmlUtils.htmlEscape(userDTO.getName()));
        Objects.requireNonNull(headerAccessor.getSessionAttributes())
                .put("room", room);

        MessageDTO messageDTO = messageMapper.messageToMessageDTO(
                messageService.hello(messageMapper.userDtoToMessage(userDTO, room)));

        messagingTemplate.convertAndSend("/topic/messages/" + room, messageDTO);

        return messageMapper.messageToMessageDTO(
                messageService.find10LastMessages(room));
    }

    @MessageMapping("/messaging/{room}")
    @SendTo("/topic/messages/{room}")
    public MessageDTO messaging(@DestinationVariable @Size(max = 9) Long room,
                                @Valid MessageDTO messageDTO) {
        return messageMapper.messageToMessageDTO(
                messageService.save(
                        messageMapper.messageDtoToMessage(messageDTO, room)));
    }

    @MessageMapping("/typing/{room}")
    @SendTo("/topic/typing/{room}")
    public Set<String> typing(@DestinationVariable @Size(max = 9) Long room,
                              @Valid TypingDTO typingDTO) {
        return messageService.typing(room, typingDTO.getUserName(), typingDTO.getIsTyping());
    }
}
