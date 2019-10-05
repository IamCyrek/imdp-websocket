package com.example.service;

import com.example.model.Message;
import com.example.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    public Message hello(final Message message) {
        message.setContent("Hello, " + message.getContent() + "!");
        return save(message);
    }

    public Message save(final Message message) {
        return messageRepository.save(message);
    }

}
