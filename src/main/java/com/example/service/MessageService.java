package com.example.service;

import com.example.model.Message;
import com.example.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    private Map<Long, Set<String>> userNameSets = new HashMap<>();

    public List<Message> find10LastMessages(Long room) {
        List<Message> messages = messageRepository.findTop10ByRoomOrderByCreationTimeDesc(room);
        Collections.reverse(messages);
        return messages;
    }

    public Message hello(final Message message) {
        message.setContent("Hello, " + message.getContent() + "!");
        return save(message);
    }

    public Message save(final Message message) {
        return messageRepository.save(message);
    }

    public Set<String> typing(Long room, String userName, Boolean isTyping) {
        if (isTyping)
            userNameSets.computeIfAbsent(room, k -> new HashSet<>()).add(userName);
        else if (userNameSets.get(room) == null)
            userNameSets.put(room, new HashSet<>());
        else
            userNameSets.get(room).remove(userName);

        return userNameSets.get(room);
    }
}
