package axon.jeju.service;

import axon.jeju.command.JejuAccountCreationCommand;
import axon.jeju.dto.JejuAccountDTO;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JejuAccountServiceImpl implements JejuAccountService {
    private final CommandGateway commandGateway;

    @Override
    public String createAccount(JejuAccountDTO accountDTO) {
        return commandGateway.sendAndWait(new JejuAccountCreationCommand(accountDTO.getAccountID(), accountDTO.getBalance()));
    }
}
