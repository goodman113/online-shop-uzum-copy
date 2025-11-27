package project.controller;

import lombok.RequiredArgsConstructor;
import project.model.create.OrderCreateDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import project.service.OrderService;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    final OrderService service;

    @GetMapping
    public String order(Model model) {
        /// find order by CurrentUser
        return  "order";
    }

    @PostMapping
    public String order(OrderCreateDto order) {
        service.create(order);
        return "redirect:/product";
    }


}
