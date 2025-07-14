package ai.shreds.application.ports;

import ai.shreds.shared.dtos.SharedQRCodeRequestDTO;
import ai.shreds.shared.dtos.SharedQRCodeResponseDTO;

public interface ApplicationOutputPortQRCodeService {

    /**
     * Generates a QR code for a given URL or content.
     *
     * @param request the QR code generation request containing content and options
     * @return response containing the generated QR code URL and metadata
     */
    SharedQRCodeResponseDTO generateQRCode(SharedQRCodeRequestDTO request);
}
