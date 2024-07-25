package com.dev.booking.Controller;

import com.dev.booking.RequestDTO.PaymentRequest;
import com.dev.booking.ResponseDTO.PaymentResponse;
import com.dev.booking.Service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private VNPayService vnPayService;
//
//    @PostMapping("/create")
//    public ResponseEntity<?> createPayment(@RequestBody PaymentRequest paymentRequest, HttpServletRequest request) throws Exception {
//        String orderId = String.valueOf(System.currentTimeMillis());
//        long amount = paymentRequest.getAmount();
//        String ipAddress = request.getRemoteAddr();
//        String paymentUrl = vnPayService.createPaymentUrl(orderId, amount, ipAddress);
//        return ResponseEntity.ok(new PaymentResponse(paymentUrl));
//    }

    @GetMapping("/return")
    public ResponseEntity<?> paymentReturn(HttpServletRequest request) throws UnsupportedEncodingException {
        Map<String, String> vnpParams = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String paramName = params.nextElement();
            String paramValue = request.getParameter(paramName);
            vnpParams.put(paramName, paramValue);
        }
            String responseCode = vnpParams.get("vnp_ResponseCode");
            String transactionNo = vnpParams.get("vnp_TransactionNo");
            String txnRef = vnpParams.get("vnp_TxnRef");
            String amount = vnpParams.get("vnp_Amount");
            if ("00".equals(responseCode)) {

                return ResponseEntity.ok("Payment Success: Transaction No: " + transactionNo + ", Amount: " + amount);
            }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment Failed: Response Code: " + responseCode);
    }


}
