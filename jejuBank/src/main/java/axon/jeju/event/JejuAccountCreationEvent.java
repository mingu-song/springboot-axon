package axon.jeju.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
@Getter
public class JejuAccountCreationEvent {
    private final String accountID;
    private final Long balance;
}
