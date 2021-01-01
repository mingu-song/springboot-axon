package axon.jeju.controller;

import axon.jeju.dto.JejuAccountDTO;
import axon.jeju.service.JejuAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JejuAccountController {
    private final JejuAccountService accountService;

    @PostMapping("/account")
    public ResponseEntity<String> createAccount(@RequestBody JejuAccountDTO accountDTO){
        return ResponseEntity.ok().body(accountService.createAccount(accountDTO));
    }
}
