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
//
//    @GetMapping("/return")
//    public ResponseEntity<?> paymentReturn(HttpServletRequest request) throws UnsupportedEncodingException {
//        Map<String, String> vnpParams = new HashMap<>();
//        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
//            String paramName = params.nextElement();
//            String paramValue = request.getParameter(paramName);
//            vnpParams.put(paramName, paramValue);
//        }
//
//        // Xử lý xác thực dữ liệu phản hồi từ VNPay
//        boolean isValid = vnPayService.validateReturnUrl(vnpParams);
//       // if (isValid) {
//            // Lấy các tham số quan trọng
//            String responseCode = vnpParams.get("vnp_ResponseCode");
//            String transactionNo = vnpParams.get("vnp_TransactionNo");
//            String txnRef = vnpParams.get("vnp_TxnRef");
//            String amount = vnpParams.get("vnp_Amount");
//
//            // Kiểm tra mã phản hồi để xác định trạng thái giao dịch
//            if ("00".equals(responseCode)) {
//                // Giao dịch thành công
//                // Bạn có thể lưu thông tin giao dịch vào cơ sở dữ liệu hoặc thực hiện các hành động khác
//                // ...
//                return ResponseEntity.ok("Payment Success: Transaction No: " + transactionNo + ", Amount: " + amount);
//            } //else {
//                // Giao dịch không thành công
//                // Xử lý logic khi thanh toán thất bại
//                // ...
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment Failed: Response Code: " + responseCode);
////            }
////        } else {
////            // Xử lý logic khi xác thực chữ ký thất bại
////            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Signature");
////        }
//    }


}
