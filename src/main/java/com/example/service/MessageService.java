package com.example.service;

import com.example.model.Message;
import com.example.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    private Set<String> userNameSet = new HashSet<>();

    public List<Message> find10LastMessages() {
        return messageRepository.findTop10ByOrderByCreationTimeDesc();
    }

    public Message hello(final Message message) {
        message.setContent("Hello, " + message.getContent() + "!");
        return save(message);
    }

    public Message save(final Message message) {
        return messageRepository.save(message);
    }

    public Set<String> typing(String userName, Boolean isTyping) {
        if (isTyping)
            userNameSet.add(userName);
        else
            userNameSet.remove(userName);

        return userNameSet;
    }
}
