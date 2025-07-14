package ai.shreds.infrastructure.external_services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ai.shreds.application.ports.ApplicationOutputPortQRCodeService;
import ai.shreds.shared.dtos.SharedQRCodeRequestDTO;
import ai.shreds.shared.dtos.SharedQRCodeResponseDTO;
import ai.shreds.infrastructure.utils.InfrastructureRetryUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InfrastructureQRCodeServiceClient implements ApplicationOutputPortQRCodeService {

    private final RestTemplate restTemplate;
    private final InfrastructureRetryUtil retryUtil;
    private final String qrCodeServiceUrl;

    public InfrastructureQRCodeServiceClient(
            @Value("${qrcode.service.url}") String qrCodeServiceUrl,
            InfrastructureRetryUtil retryUtil) {
        this.qrCodeServiceUrl = qrCodeServiceUrl;
        this.retryUtil = retryUtil;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public SharedQRCodeResponseDTO generateQRCode(SharedQRCodeRequestDTO request) {
        try {
            return retryUtil.executeWithRetry(() ->
                restTemplate.postForObject(
                    qrCodeServiceUrl + "/generate",
                    request,
                    SharedQRCodeResponseDTO.class
                )
            );
        } catch (Exception e) {
            log.error("Error generating QR code for content: {}", request.getContent(), e);
            return SharedQRCodeResponseDTO.builder()
                .success(false)
                .qrCodeUrl(null)
                .expiresAt(null)
                .build();
        }
    }
}
