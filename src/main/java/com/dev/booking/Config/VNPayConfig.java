package com.dev.booking.Config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "vnpay")
public class VNPayConfig {
    @Value("${vnpay.vnpTmnCode}")
    private String vnpTmnCode;

    @Value("${vnpay.vnpHashSecret}")
    private String vnpHashSecret;

    @Value("${vnpay.vnpUrl}")
    private String vnpUrl;

    @Value("${vnpay.vnpReturnUrl}")
    private String vnpReturnUrl;


    public void setVnpTmnCode(String vnpTmnCode) {
        this.vnpTmnCode = vnpTmnCode;
    }

    public void setVnpHashSecret(String vnpHashSecret) {
        this.vnpHashSecret = vnpHashSecret;
    }

    public void setVnpUrl(String vnpUrl) {
        this.vnpUrl = vnpUrl;
    }

    public void setVnpReturnUrl(String vnpReturnUrl) {
        this.vnpReturnUrl = vnpReturnUrl;
    }
}
