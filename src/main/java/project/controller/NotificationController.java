package project.controller;

import lombok.RequiredArgsConstructor;
import project.model.create.NotificationCreateDto;
import project.model.dto.NotificationDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    final NotificationService service;
    @GetMapping
    public String notifications(Model model,@RequestParam(name = "search",required = false) String search, @RequestParam(name = "fromDate",  required = false) String fromDate, @RequestParam(name = "toDate", required = false) String toDate) {
        LocalDateTime from = LocalDateTime.parse(fromDate);
        LocalDateTime to = LocalDateTime.parse(toDate);
        List<NotificationDto> all = service.getAll(search, from, to);
        model.addAttribute("notifications", all);
        return "notification/notifications";
    }

    @PostMapping
    public String notification(NotificationCreateDto dto) {
        service.create(dto);
        return "redirect:/notification";
    }


}
