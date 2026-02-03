package com.fpoly.controller.cilent;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@Controller
@RequestMapping("/dbu")
public class DatatableTestController {
	@GetMapping("/")
	public String list(Model model) {
		model.addAttribute("content", "/view/client/truyen/index.html");
		return "/layout/cilent_base";
	}
    
}
