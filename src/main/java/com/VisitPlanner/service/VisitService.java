package com.VisitPlanner.service;

import com.VisitPlanner.entity.User;
import com.VisitPlanner.entity.Visit;
import com.VisitPlanner.repository.UserRepository;
import com.VisitPlanner.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VisitService {

    @Autowired
    VisitRepository repository;

    @Autowired
    UserRepository userRepository;

    public final LocalDateTime START_DATE = LocalDateTime.of(2023, 9, 1, 8, 00);
    public final int MINUTES_FOR_VISIT = 20;
    public final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    public List<Visit> getAllVisits() {
        return repository.findAll();
    }

    public Optional<Visit> getById(Integer id){
        return repository.findById(id);
    }

    public void createVisit(Visit visit) {
        visit.setVisitNumber(generateTimeStamp());
        visit.setReservationTime(getCurrentDateTime());
        visit.setReservedTime(generateReservedTime(visit.getUser().getId()));
        visit.setStatus(Visit.Status.Waiting);
        visit.setStatusChangeDate(getCurrentDateTime());
        System.out.println("Created visit " + visit);
        repository.save(visit);
    }

    public String generateReservedTime(Integer userId){
        List<Visit> visits = getVisitListByUserId(userId);
        if (!visits.isEmpty()){
            String lastReservedTime = findNewestVisit(userId);
            LocalDateTime reservedDateTime = convertStringToDateTime(lastReservedTime).plusMinutes(MINUTES_FOR_VISIT);
            return convertDateTimeToString(reservedDateTime);
        }
        return convertDateTimeToString(START_DATE);
    }

    public LocalDateTime convertStringToDateTime(String dateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        return LocalDateTime.parse(dateTime, formatter);
    }

    public String convertDateTimeToString(LocalDateTime dateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        return dateTime.format(formatter);
    }

    public String findNewestVisit(Integer userId){
        List<Visit> visits = getVisitListByUserId(userId);
        return visits.get(visits.size() - 1).getReservedTime();
    }

    public Visit getByVisitNumber(String visitNumber) {
        return repository.findByVisitNumberIgnoreCase(visitNumber);
    }

    public String generateTimeStamp(){
        final String TIME_STAMP_FORMAT = "MMddHHmmssms";
        return new SimpleDateFormat(TIME_STAMP_FORMAT).format(new java.util.Date());
    }

    public void checkStatus(Integer id, Visit.Status newStatus){
        Optional<Visit> visitOptional = repository.findById(id);
        Visit.Status currentStatus = visitOptional.get().getStatus();
        switch (newStatus){
            case Started:
                if (!statusStartedExists(visitOptional.get().getUser())&&(currentStatus.equals(Visit.Status.Waiting))){
                    updateStatus(visitOptional.get(), newStatus);
                }
                break;
            case Finished:
                if (currentStatus.equals(Visit.Status.Started)){
                    updateStatus(visitOptional.get(), newStatus);
                }
                break;
            case Canceled:
                if (currentStatus.equals(Visit.Status.Waiting)) {
                    updateStatus(visitOptional.get(), newStatus);
                }
                break;
        }
    }

    public void updateStatus(Visit visit, Visit.Status newStatus){
        visit.setStatus(newStatus);
        visit.setStatusChangeDate(getCurrentDateTime());
        repository.save(visit);
    }

    public boolean statusStartedExists(User user){
        System.out.println(user);
        List<Visit> visits = repository.findByUserOrderByStatusDesc(Optional.ofNullable(user));
        for (Visit visit : visits){
            if(visit.getStatus().equals(Visit.Status.Started)) {
                return true;
            }
        }
        return false;
    }

    public List<Visit> getServiceDeskVisits() {
        List<Visit> visitsStarted = repository.findAllByStatus(Visit.Status.Started);
        List<Visit> visitsWaiting = repository.findAllByStatus(Visit.Status.Waiting);
        List<Visit> visitsToShow = new ArrayList<>(visitsStarted);
        int itemsFromWaitingList = 7;
        for (int i = 0; i < itemsFromWaitingList && i < visitsWaiting.size(); i++) {
            visitsToShow.add(visitsWaiting.get(i));
        }
        System.out.println(visitsToShow);
        return visitsToShow;
    }

    public String getCurrentDateTime(){
        return convertDateTimeToString(LocalDateTime.now());
    }

    public List<Visit> getVisitListByUserId(Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        List<Visit> visits = repository.findByUserOrderByStatusDesc(user);
        List<Visit> visitsToShow = new ArrayList<>();
        for (Visit visit : visits){
            if (visit.getStatus().equals(Visit.Status.Started) || visit.getStatus().equals(Visit.Status.Waiting)) {
                visitsToShow.add(visit);
            }
        }
        return visitsToShow;
    }

    public String calculateTimeUntilVisit(String reservedTime){
        Duration duration = Duration.between(LocalDateTime.now(), convertStringToDateTime(reservedTime));
        System.out.println(duration);
        long days = duration.toDays();
        long hours = duration.toHours() - days * 24;
        long minutes = duration.toMinutes() % 60;
        String timeUntilVisit = days + " days " + hours + " hours " + minutes + " minutes";
        return timeUntilVisit;
    }

    public int getPlaceInWaitingLine(Visit currentVisit, Integer userId) {
        List<Visit> visitsInLine = getVisitListByUserId(userId);
        int placeInLine = 0;
        for (Visit visit : visitsInLine){
            placeInLine++;
            if (visit.equals(currentVisit)){
                break;
            }
        }
        return placeInLine;
    }
}
