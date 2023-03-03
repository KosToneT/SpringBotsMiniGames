package com.develop.SpringMiniGames;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.develop.SpringMiniGames.Bots.Bot;
import com.develop.SpringMiniGames.Reports.ReportRepo;




@Controller
public class MainPageController {
    @Autowired
    private BotManager botManager;

    @Autowired
    private ReportRepo reportRepo;

    @GetMapping("/")
    public String mainPage(Model model){
        model.addAttribute("VKONLINE", botManager.getBots().get(0).isOnline()?"Online":"Offline");
        model.addAttribute("TGONLINE", botManager.getBots().get(1).isOnline()?"Online":"Offline");
        return "index";
    }

    @GetMapping("/land")
    public String newPage(Model model){
        return "landing";
    }

    @GetMapping("/reports")
    public String getReport(Model model){
        model.addAttribute("reports", reportRepo.findAll());
        return "reports";
    }

}
