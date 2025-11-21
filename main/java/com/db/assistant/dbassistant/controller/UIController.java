package com.db.assistant.dbassistant.controller;

import com.db.assistant.dbassistant.dto.AIRequest;
import com.db.assistant.dbassistant.service.IntentHandlerService;
import com.db.assistant.dbassistant.service.OllamaIntentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UIController {

    @Autowired
    private OllamaIntentService ollamaIntentService;

    @Autowired
    private IntentHandlerService intentHandlerService;

    @PostMapping("/askMe")
    public String askMe(@RequestParam String question, Model model) {
        model.addAttribute("question", question);
        try {
            AIRequest intent = ollamaIntentService.getIntent(question);
            model.addAttribute("intent", intent);

            String answer = intentHandlerService.handleIntent(intent);
            model.addAttribute("answer", answer);
        } catch (Exception e) {
            model.addAttribute("answer", "Error: " + e.getMessage());
        }
        return "index";
    }
}