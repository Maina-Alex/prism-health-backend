package com.prismhealth.services;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import com.prismhealth.Models.Help;
import com.prismhealth.Models.Issues;
import com.prismhealth.Models.PushContent;
import com.prismhealth.Models.Users;
import com.prismhealth.repository.UserRepository;
import com.prismhealth.repository.HelpRepo;
import com.prismhealth.repository.IssuesRepo;
import com.prismhealth.util.HelpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class HelpService {
    @Autowired
    HelpRepo helpRepo;
    @Autowired
    UserRepository usersRepo;
    @Autowired
    private IssuesRepo issuesRepo;
    @Autowired
    private NotificationService notificationService;

    private Logger log = LoggerFactory.getLogger(HelpService.class);

    public Help addHelp(Principal principal, Help help) {
        Optional<Users> user = Optional.ofNullable(usersRepo.findByPhone(principal.getName()));
        log.info("Adding new user help");

        if (user.isPresent()) {
            List<Issues> issues = issuesRepo.findByUserIdAndStatus(user.get().getPhone(), HelpStatus.OPEN,
                    Sort.by("timestamp").descending());
            Issues i = null;
            if (issues.isEmpty()) {
                i = new Issues();
                i.setStatus(HelpStatus.OPEN);
                i.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
                i = issuesRepo.save(i);

            } else {
                i = issues.get(0);
            }
            help.setIssueId(i.getId());
            help.setStatus(HelpStatus.OPEN);
            help.setUserId(user.get().getPhone());
            help.setEmail(user.get().getEmail());
            help.setPhoneNumber(user.get().getPhone());
            help.setFullname(user.get().getFirstName() + " " + user.get().getSecondName());
            help.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            notificationService.addHelpNotification(user.get().getPhone());
            return helpRepo.save(help);
        } else
            return null;

    }

    public List<Help> findAllUserHelp(Principal principal) {
        Optional<Users> user = Optional.ofNullable(usersRepo.findByPhone(principal.getName()));
        if (user.isPresent())
            return helpRepo.findAllByUserId(user.get().getPhone(), Sort.by("timestamp").descending());
        else
            return new ArrayList<>();
    }

    public List<Issues> getAllIssues() {
        return issuesRepo.findAll(Sort.by("timestamp").descending()).stream().map(this::populateIssue)
                .collect(Collectors.toList());

    }

    private Issues populateIssue(Issues i) {
        i.setHelpMessages(helpRepo.findAllByIssueId(i.getId(), Sort.by("timestamp").descending()));

        Optional<Users> user = usersRepo.findById(i.getUserId());
        if (user.isPresent()) {
            i.setFullname(user.get().getFirstName() + " " + user.get().getSecondName());
            i.setEmail(user.get().getEmail());
            i.setPhoneNumber(user.get().getPhone());
        }
        if (!i.getHelpMessages().isEmpty()) {
            int size = i.getHelpMessages().size();
            i.setMessage(i.getHelpMessages().get(size - 1).getMessage());
        }

        return i;
    }

    public Issues upDateIssues(Issues issues) {
        Issues i = issuesRepo.save(issues);
        helpRepo.saveAll(i.getHelpMessages());
        return i;
    }

    public Issues getIssuesById(String id) {
        Optional<Issues> op = issuesRepo.findById(id);
        if (op.isPresent())
            return op.get();
        else
            return null;
    }

    public Issues addReply(Help help, Principal principal) {
        log.info("Added new user help reply");
        help.setEmail(principal.getName());
        help.setRead(true);
        help.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        helpRepo.save(help);
        Optional<Issues> i = issuesRepo.findById(help.getIssueId());
        if (i.isPresent()) {

            helpRepo.findAllByIssueId(i.get().getId(), Sort.by("timestamp")).forEach(h -> {
                h.setRead(true);
                helpRepo.save(h);
            });
            notificationService.addHelpReplyNotification(i.get().getUserId());
            return populateIssue(i.get());

        } else
            return null;

    }

    public Issues changeStatus(Issues issues, Principal principal) {
        Optional<Issues> op = issuesRepo.findById(issues.getId());
        if (op.isPresent()) {
            Issues i = op.get();

            if (issues.getStatus().equals(HelpStatus.ClOSED)) {

                i.setClosedBy(principal.getName());
                i.setClosedOn(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));

            }
            i.setStatus(issues.getStatus());

            helpRepo.findAllByIssueId(i.getId(), Sort.unsorted()).stream().map(h -> {
                h.setStatus(i.getStatus());
                h.setRead(true);
                return h;

            }).forEach(helpRepo::save);

            issuesRepo.save(i);
            PushContent content = new PushContent("Help Request", "Your help request status has changed");
           // notificationService.genericUserNotification(i.getUserId(), content);
            return populateIssue(i);
        } else
            return null;

    }

}
