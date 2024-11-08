package com.example.kkbackend.controllers;

import com.example.kkbackend.dtos.*;
import com.example.kkbackend.entities.RegistrationInfo;
import com.example.kkbackend.repositories.EventRepository;
import com.example.kkbackend.repositories.MemberRepository;
import com.example.kkbackend.repositories.RegistrationInfoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;
import com.example.kkbackend.entities.Member;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MainController {
    private final String TELEGRAM_TOKEN = "7144526471:AAG2XsY2tw9lJUVbx_x4z2Rhssiuk6IAaCg";
    private final MemberRepository memberRepository;
    private final RegistrationInfoRepository registrationInfoRepository;
    private final EventRepository eventRepository;

    @Value("${current-event}")
    private String currentEvent;
    private static final String GOOGLE_FORM_BASE_URL = "https://docs.google.com/forms/d/1niEmYw58porceUl9-kvFBif9RNvM3Ywe1cSijPeqVOY/viewform?usp=pp_url";

    @GetMapping("main")
    public RedirectView getAuthRequest(AuthenticatedUserDto authenticatedUserDto) throws IllegalAccessException {
        //boolean result = verifyAuth(authenticatedUserDto);
        Member member;
        Optional<Member> memberOptional = memberRepository.getMemberByUserName(authenticatedUserDto.getUsername());
        if (memberOptional.isEmpty()) {
            member = Member.builder()
                    .userName(authenticatedUserDto.getUsername())
                    .photoUrl(authenticatedUserDto.getPhotoUrl())
                    .build();
            memberRepository.save(member);
        }
        RedirectView redirectView = new RedirectView();
        Optional<RegistrationInfo> registrationInfoOptional = registrationInfoRepository.getRegistrationInfoByContact("@" + authenticatedUserDto.getUsername());
        if (registrationInfoOptional.isPresent()) {
            RegistrationInfo registrationInfo = registrationInfoOptional.get();
            redirectView.setUrl(GOOGLE_FORM_BASE_URL +
                    "&entry.384833544=" + URLEncoder.encode(registrationInfo.getName(), StandardCharsets.UTF_8) +
                    "&entry.2064892854=" + URLEncoder.encode(registrationInfo.getGender(), StandardCharsets.UTF_8) +
                    "&entry.1243795402=" + URLEncoder.encode("Нет", StandardCharsets.UTF_8) +
                    "&entry.518733161=" + registrationInfo.getContact());
        } else {
            redirectView.setUrl(GOOGLE_FORM_BASE_URL +
                    "&entry.518733161=" + "@" + authenticatedUserDto.getUsername());
        }
        return redirectView;
    }

    @GetMapping("members")
    public List<MemberDto> getMembers() {
        return eventRepository.getById(UUID.fromString(currentEvent)).getMembers()
                .stream()
                .map(member -> MemberDto.builder()
                        .userName(member.getUserName())
                        .freshBlood(member.isFreshBlood())
                        .build())
                .toList();
    }

    @PostMapping("register")
    @Transactional
    public RegisterResponseDto register(@RequestBody RegisterDto registerDto) {
        var member = memberRepository.getMemberByUserName(registerDto.username());
        if (member.isEmpty()) {
            member = Optional.of(memberRepository.save(
                    Member.builder()
                            .userName(registerDto.username())
                            .events(new HashSet<>())
                            .freshBlood(true)
                            .build()));
        }
        var event = eventRepository.getById(UUID.fromString(currentEvent));
        if (event.getMembers().contains(member.get())) {
            return RegisterResponseDto.builder().isAlreadyRegistered(true).build();
        }
        event.getMembers().add(member.get());
        member.get().getEvents().add(event);
        if (member.get().isFreshBlood() && member.get().getEvents().size() > 1) {
            member.get().setFreshBlood(false);
            memberRepository.save(member.get());
        }
        event = eventRepository.save(event);

        return RegisterResponseDto.builder()
                .membersCount(event.getMembers().size())
                .isAlreadyRegistered(false)
                .messages(event.getTelegramMessages()
                        .stream()
                        .map(m -> TelegramMessageDto.builder()
                                .messageId(m.getMessageId())
                                .chatId(m.getChatId())
                                .build()).collect(Collectors.toList()))
                .build();
    }

    @PostMapping("unregister")
    @Transactional
    public RegisterResponseDto unregister(@RequestBody RegisterDto registerDto) {
        var member = memberRepository.getMemberByUserName(registerDto.username());
        if (member.isEmpty()) {
            member = Optional.of(memberRepository.save(
                    Member.builder()
                            .userName(registerDto.username())
                            .events(new HashSet<>())
                            .build()));
        }
        var event = eventRepository.getById(UUID.fromString(currentEvent));

        if (!event.getMembers().contains(member.get())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "registration not found"
            );
        }
        event.getMembers().remove(member.get());
        member.get().getEvents().remove(event);
        event = eventRepository.save(event);

        return RegisterResponseDto.builder()
                .membersCount(event.getMembers().size())
                .isAlreadyRegistered(false)
                .messages(event.getTelegramMessages()
                        .stream()
                        .map(m -> TelegramMessageDto.builder()
                                .messageId(m.getMessageId())
                                .chatId(m.getChatId())
                                .build()).collect(Collectors.toList()))
                .build();
    }

    @PostMapping("update")
    public boolean updateRegistrationInfo(@RequestBody RegistrationInfoDto registrationInfoDto) {
        Optional<Member> member = memberRepository.getMemberByUserName(registrationInfoDto.getContact().replace("@", ""));
        if (member.isPresent()) {
            RegistrationInfo registrationInfo;
            Optional<RegistrationInfo> registrationInfoOptional = registrationInfoRepository.getRegistrationInfoByContact(registrationInfoDto.getContact());
            if (registrationInfoOptional.isPresent()) {
                registrationInfo = registrationInfoOptional.get();
                registrationInfo.setName(registrationInfoDto.getName());
                registrationInfo.setGender(registrationInfoDto.getGender());
                registrationInfo.setContact(registrationInfoDto.getContact());
            } else {
                registrationInfo = RegistrationInfo.builder()
                        .member(member.get())
                        .name(registrationInfoDto.getName())
                        .gender(registrationInfoDto.getGender())
                        .contact(registrationInfoDto.getContact())
                        .build();
            }
            registrationInfoRepository.save(registrationInfo);
        }
        return true;
    }

    private boolean verifyAuth(AuthenticatedUserDto authenticatedUserDto) throws IllegalAccessException {
        Map<String, Object> request = convert(authenticatedUserDto);
        String hash = (String) request.get("hash");
        request.remove("hash");

        // Prepare the string
        String str = request.entrySet().stream()
                .sorted((a, b) -> a.getKey().compareToIgnoreCase(b.getKey()))
                .map(kvp -> kvp.getKey() + "=" + kvp.getValue())
                .collect(Collectors.joining("\n"));

        try {
            SecretKeySpec sk = new SecretKeySpec(
                    // Get SHA 256 from telegram token
                    MessageDigest.getInstance("SHA-256").digest(TELEGRAM_TOKEN.getBytes(StandardCharsets.UTF_8)
                    ), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(sk);

            byte[] result = mac.doFinal(str.getBytes(StandardCharsets.UTF_8));

            // Convert the result to hex string
            // Like https://stackoverflow.com/questions/9655181
            String resultStr = new String(Hex.encode(result));

            // Compare the result with the hash from body
            if(hash.compareToIgnoreCase(resultStr) == 0) {

                // Do other things like create a user and JWT token
                //return ResponseEntity.ok("ok");
                return true;
            } else {
                /*
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        new MessageResponse("Login info hash mismatch")
                );
                 */
                return false;
            }
        } catch (Exception e) {
            //logger.error(e.getMessage(), e);
            /*
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new MessageResponse("Server error while authenticating")
            );
            */
            return false;
        }
    }

    public static Map<String, Object> convert(Object object) throws IllegalAccessException {
        Map<String, Object> parameters = new HashMap<>();
        for (Field declaredField : object.getClass().getDeclaredFields()) {
            declaredField.setAccessible(true);
            parameters.put(declaredField.getName(), declaredField.get(object));
        }
        return parameters;
    }
}
