package com.VisitPlanner.controller;

import com.VisitPlanner.entity.User;
import com.VisitPlanner.entity.Visit;
import com.VisitPlanner.service.UserService;
import com.VisitPlanner.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping("/visits")
public class VisitController {

    @Autowired
    VisitService service;

    @Autowired
    UserService userService;

    private static final java.util.logging.Logger LOGGER = Logger.getLogger(VisitController.class.getName());

    @GetMapping("/list")
    public String showAllVisits(Model model){
        List<Visit> visits = service.getAllVisits();
        model.addAttribute("visits", visits);
        return "visits/list";
    }

    @GetMapping("/addForm")
    public String addVisitForm(Model model){
        List<User> users = userService.getAllUsers();
        Visit visit = new Visit();
        model.addAttribute("visit", visit);
        model.addAttribute("users", users);
        return "visits/add-form";
    }

    @PostMapping("/create")
    public String createVisit(@ModelAttribute("visit") Visit visit, Model model){
        service.createVisit(visit);
        String visitNumber = visit.getVisitNumber();
        return findByVisitNumber(visitNumber, model);
    }

    @GetMapping("/findForm")
    public String findByNumberForm(Model model){
        String visitNumber = "";
        model.addAttribute("visitNumber", visitNumber);
        return "visits/find-form";
    }

    @GetMapping("/visitNumber")
    public String findByVisitNumber(@RequestParam("visitNumber") String visitNumber, Model model){
        Visit visit = service.getByVisitNumber(visitNumber);
        LOGGER.info("findByVisitNumber: " + visit);
        if (visit == null) return "visits/oops";
        if ((visit.getStatus().equals(Visit.Status.Waiting))||(visit.getStatus().equals(Visit.Status.Started))) {
            model.addAttribute("visitId", visit.getId());
            model.addAttribute("visitNumber", visit.getVisitNumber());
            model.addAttribute("user", visit.getUser().getName());
            model.addAttribute("reservationTime", visit.getReservationTime());
            model.addAttribute("reservedTime", visit.getReservedTime());
            model.addAttribute("status", visit.getStatus());
            model.addAttribute("statusDate", visit.getStatusChangeDate());
            model.addAttribute("placeInLine", service.getPlaceInWaitingLine(visit, visit.getUser().getId()));
            String timeUntilVisit = service.calculateTimeUntilVisit(visit.getReservedTime());
            model.addAttribute("timeLeft", timeUntilVisit);
            return "visits/find";
        } else
            return "visits/oops";
    }

    @GetMapping("/user/tasks")
    public String findByUserName(@RequestParam ("username") String userName, Model model ){
        User user = userService.findByUserName(userName);
        List<Visit> visits = service.getVisitListByUserId(user.getId());
        boolean statusStartedExists = service.statusStartedExists(user);
        model.addAttribute("visits", visits);
        model.addAttribute("statusStartedExists", statusStartedExists);
        return "visits/user-tasks";
    }

    @PostMapping("/user/start")
    public String startVisit(@RequestParam("visitNumber") String visitNumber) {
        String userName = service.getByVisitNumber(visitNumber).getUser().getName();
        service.checkStatus(visitNumber, Visit.Status.Started);
        return "redirect:/visits/user/tasks?username=" + userName;
    }

    @PostMapping("/user/finish")
    public String finishVisit(@RequestParam("visitNumber") String visitNumber) {
        String userName = service.getByVisitNumber(visitNumber).getUser().getName();
        service.checkStatus(visitNumber, Visit.Status.Finished);
        return "redirect:tasks?username=" + userName;
    }

    @GetMapping("/admin/serviceDesk")
    public String showServiceDesk(Model model){
        List<Visit> visits = service.getServiceDeskVisits();
        model.addAttribute("visits", visits);
        return "visits/service-desk";
    }

    @PostMapping("/cancel")
    public String cancelVisit(@RequestParam("visitNumber") String visitNumber) {
        service.checkStatus(visitNumber, Visit.Status.Canceled);
        return "visits/canceled";
    }

    @GetMapping("/logged")
    public String logged() {
        return "redirect:visits/logged";
    }

}