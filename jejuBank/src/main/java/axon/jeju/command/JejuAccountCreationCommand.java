package axon.jeju.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class JejuAccountCreationCommand {
    @TargetAggregateIdentifier
    private String accountID;
    private Long balance;
}
