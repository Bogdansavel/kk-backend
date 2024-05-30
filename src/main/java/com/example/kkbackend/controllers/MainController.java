package com.example.kkbackend.controllers;

import com.example.kkbackend.dtos.AuthenticatedUserDto;
import com.example.kkbackend.dtos.RegistrationInfoDto;
import com.example.kkbackend.entities.RegistrationInfo;
import com.example.kkbackend.repositories.MemberRepository;
import com.example.kkbackend.repositories.RegistrationInfoRepository;
import com.example.kkbackend.util.GoogleSheetsAuthUtil;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.sheets.v4.Sheets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import com.example.kkbackend.entities.Member;

@RestController
@RequiredArgsConstructor
public class MainController {
    private final String TELEGRAM_TOKEN = "7144526471:AAG2XsY2tw9lJUVbx_x4z2Rhssiuk6IAaCg";
    private final MemberRepository memberRepository;
    private final RegistrationInfoRepository registrationInfoRepository;
    private final GoogleSheetsAuthUtil googleSheetsAuthUtil;

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
            redirectView.setUrl("https://docs.google.com/forms/d/1ZaymP4_t-XKoqLo0LOeWudbTEWidfLXJVzjoLbch_e8/viewform?usp=pp_url&" +
                    "entry.384833544=" + URLEncoder.encode(registrationInfo.getName(), StandardCharsets.UTF_8) +
                    "&entry.2064892854=" + URLEncoder.encode(registrationInfo.getGender(), StandardCharsets.UTF_8) +
                    "&entry.1243795402=" + URLEncoder.encode("Нет", StandardCharsets.UTF_8) +
                    "&entry.518733161=" + registrationInfo.getContact());
        } else {
            redirectView.setUrl("https://docs.google.com/forms/d/1ZaymP4_t-XKoqLo0LOeWudbTEWidfLXJVzjoLbch_e8/viewform?usp=pp_url&/viewform?usp=pp_url&" +
                    "&entry.518733161=" + "@" + authenticatedUserDto.getUsername());
        }
        return redirectView;
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

    @PostMapping("cancel")
    public boolean cancel(@RequestBody String nickname) throws GeneralSecurityException, IOException {
        googleSheetsAuthUtil.deleteRow(nickname);
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
