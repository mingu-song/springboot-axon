package axon.events.transfer;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@Getter
public class TransferDeniedEvent {
    private String srcAccountID;
    private String dstAccountID;
    private String transferID;
    private Long amount;
    private String description;
}
