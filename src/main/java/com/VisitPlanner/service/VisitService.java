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
import java.util.logging.Logger;

@Service
public class VisitService {

    @Autowired
    VisitRepository repository;

    @Autowired
    UserRepository userRepository;

    public LocalDateTime START_DATE = LocalDateTime.now().plusDays(3).withHour(8).withMinute(0);

    public final int MINUTES_FOR_VISIT = 20;
    public final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
    private static final java.util.logging.Logger LOGGER = Logger.getLogger(VisitService.class.getName());

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
        LOGGER.info("createVisit: " + visit);
        repository.save(visit);
    }

    public String generateReservedTime(Integer userId) {
        List<Visit> visits = getVisitListToCreateVisit(userId);
        if (!visits.isEmpty()){
            String lastReservedTime = visits.get(visits.size()-1).getReservedTime();
            LocalDateTime reservedDateTime = convertStringToDateTime(lastReservedTime).plusMinutes(MINUTES_FOR_VISIT);
            return convertDateTimeToString(reservedDateTime);
        }
        return convertDateTimeToString(START_DATE);
    }

    public LocalDateTime convertStringToDateTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        return LocalDateTime.parse(dateTime, formatter);
    }

    public String convertDateTimeToString(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        return dateTime.format(formatter);
    }

    public Visit getByVisitNumber(String visitNumber) {
        return repository.findByVisitNumberIgnoreCase(visitNumber);
    }

    public String generateTimeStamp() {
        final String TIME_STAMP_FORMAT = "MMddHHmmssms";
        return new SimpleDateFormat(TIME_STAMP_FORMAT).format(new java.util.Date());
    }

    public void checkStatus(String visitNumber, Visit.Status newStatus) {
        Visit visit = repository.findByVisitNumberIgnoreCase(visitNumber);
        Visit.Status currentStatus = visit.getStatus();
        switch (newStatus){
            case Started:
                if (!statusStartedExists(visit.getUser())&&(currentStatus.equals(Visit.Status.Waiting))){
                    updateStatus(visit, newStatus);
                }
                break;
            case Finished:
                if (currentStatus.equals(Visit.Status.Started)){
                    updateStatus(visit, newStatus);
                }
                break;
            case Canceled:
                if (currentStatus.equals(Visit.Status.Waiting)) {
                    updateStatus(visit, newStatus);
                }
                break;
        }
    }

    public void updateStatus(Visit visit, Visit.Status newStatus) {
        visit.setStatus(newStatus);
        visit.setStatusChangeDate(getCurrentDateTime());
        repository.save(visit);
    }

    public boolean statusStartedExists(User user) {
        List<Visit> visits = repository.findByUserOrderByStatusDescReservedTimeAsc(Optional.ofNullable(user));
        for (Visit visit : visits){
            if(visit.getStatus().equals(Visit.Status.Started)) {
                return true;
            }
        }
        return false;
    }

    public List<Visit> getServiceDeskVisits() {
        List<Visit> visitsStarted = repository.findAllByStatusOrderByReservedTimeAsc(Visit.Status.Started);
        List<Visit> visitsWaiting = repository.findAllByStatusOrderByReservedTimeAsc(Visit.Status.Waiting);
        List<Visit> visitsToShow = new ArrayList<>(visitsStarted);
        int itemsFromWaitingList = 7;
        for (int i = 0; i < itemsFromWaitingList && i < visitsWaiting.size(); i++) {
            visitsToShow.add(visitsWaiting.get(i));
        }
        LOGGER.info("getServiceDeskVisits: " + visitsToShow);
        return visitsToShow;
    }

    public String getCurrentDateTime(){
        return convertDateTimeToString(LocalDateTime.now());
    }

    public List<Visit> getVisitListByUserId(Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        List<Visit> visits = repository.findByUserOrderByStatusDescReservedTimeAsc(user);
        List<Visit> visitsToShow = new ArrayList<>();
        for (Visit visit : visits){
            if (visit.getStatus().equals(Visit.Status.Started) || visit.getStatus().equals(Visit.Status.Waiting)) {
                visitsToShow.add(visit);
            }
        }
        return visitsToShow;
    }

    public List<Visit> getVisitListToCreateVisit(Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        List<Visit> visits = repository.findByUserOrderByReservedTimeAsc(user);
        List<Visit> visitsToUse = new ArrayList<>();
        for (Visit visit : visits){
            if (!visit.getStatus().equals(Visit.Status.Canceled)) {
                visitsToUse.add(visit);
            }
        }
        return visitsToUse;
    }

    public String calculateTimeUntilVisit(String reservedTime) {
        Duration duration = Duration.between(LocalDateTime.now(), convertStringToDateTime(reservedTime));
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
