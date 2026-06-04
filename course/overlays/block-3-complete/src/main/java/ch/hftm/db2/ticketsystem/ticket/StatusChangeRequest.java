package ch.hftm.db2.ticketsystem.ticket;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Eingabe für einen Statuswechsel")
class StatusChangeRequest {

    @NotBlank
    @Schema(description = "Neuer Ticketstatus", example = "waiting")
    private String status;

    @Schema(description = "Begründung oder kurzer Kommentar zum Statuswechsel", example = "Rueckfrage an Benutzer gesendet")
    private String reason;
}
