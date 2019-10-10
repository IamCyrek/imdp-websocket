package com.example.listener;

import com.example.controller.dto.MessageDTO;
import com.example.controller.mapper.MessageMapper;
import com.example.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Date;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;
    
    private final MessageService messageService;
    
    private final MessageMapper messageMapper;

    @EventListener
    public void handleWebSocketDisconnectListener(
            SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor =
                StompHeaderAccessor.wrap(event.getMessage());

        String userName =
                (String) Objects.requireNonNull(
                        headerAccessor.getSessionAttributes()).get("userName");
        Long room =
                (Long) Objects.requireNonNull(
                        headerAccessor.getSessionAttributes()).get("room");

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setUserName("");
        messageDTO.setContent("Bye, " + userName + ".");
        messageDTO.setCreationTime(new Date());

        messageService.save(messageMapper.messageDtoToMessage(messageDTO, room));
        messageService.typing(room, userName, false);

        messagingTemplate.convertAndSend("/topic/messages/" + room, messageDTO);
    }

}
