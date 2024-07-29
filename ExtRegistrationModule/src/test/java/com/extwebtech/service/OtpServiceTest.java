package com.extwebtech.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import com.extwebtech.registration.util.OtpService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private OtpService otpService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(otpService, "accountSid", "yourTwilioAccountSid");
        ReflectionTestUtils.setField(otpService, "authToken", "yourTwilioAuthToken");
        ReflectionTestUtils.setField(otpService, "fromNumber", "yourTwilioFromNumber");
        ReflectionTestUtils.setField(otpService, "fromEmail", "yourFromEmail");
    }

    @Test
    void generateOtp_shouldGenerateFourDigitOtp() {
        String otp = otpService.generateOtp();
        assertNotNull(otp);
        assertTrue(otp.matches("\\d{4}"));
    }

//    @Test
//    void sendOtpSms_shouldSendSmsSuccessfully() {
//        // Mock Twilio initialization
//        Twilio.init("yourTwilioAccountSid", "yourTwilioAuthToken");
//
//        // Mock Twilio Message creation
//        MessageCreator messageCreator = new MessageCreator(new PhoneNumber("validPhoneNumber"),
//                new PhoneNumber("yourTwilioFromNumber"), "Your OTP is: 1234");
//        Message message = messageCreator.create();
//
//        assertEquals("1234", otpService.sendOtpSms("validPhoneNumber", "1234"));
//    }

    @Test
    void sendOtpByEmail_shouldSendEmailSuccessfully() {
        // Mock JavaMailSender
        SimpleMailMessage[] sentMessage = {null};  // Declare as an array to make it effectively final

        try {
            doAnswer(invocation -> {
                sentMessage[0] = (SimpleMailMessage) invocation.getArguments()[0];
                return null;
            }).when(javaMailSender).send(any(SimpleMailMessage.class));
        } catch (Exception e) {
            fail("Exception not expected");
        }

        assertEquals("1234", otpService.sendOtpByEmail("valid@example.com", "1234"));
        assertNotNull(sentMessage[0]);
        assertEquals("valid@example.com", sentMessage[0].getTo()[0]);
        assertEquals("Your OTP", sentMessage[0].getSubject());
        assertEquals("Your OTP is: 1234", sentMessage[0].getText());
        assertEquals("yourFromEmail", sentMessage[0].getFrom());
    }


    @Test
    void storeGeneratedOtp_shouldStoreOtpInMap() {
        String target = "validTarget";
        String otp = "5678";
        otpService.storeGeneratedOtp(target, otp);

        Map<String, String> otpStorage = (Map<String, String>) ReflectionTestUtils.getField(otpService, "otpStorage");

        assertNotNull(otpStorage);
        assertTrue(otpStorage.containsKey(target));
        assertEquals(otp, otpStorage.get(target));
    }

    @Test
    void validateOtp_withValidOtp_shouldReturnTrue() {
        String target = "validTarget";
        String otp = "5678";
        otpService.storeGeneratedOtp(target, otp);

        assertTrue(otpService.validateOtp(target, otp));
    }

    @Test
    void validateOtp_withInvalidOtp_shouldReturnFalse() {
        String target = "validTarget";
        String storedOtp = "5678";
        String enteredOtp = "1234";
        otpService.storeGeneratedOtp(target, storedOtp);

        assertFalse(otpService.validateOtp(target, enteredOtp));
    }

    @Test
    void validateOtp_withNonExistentTarget_shouldReturnFalse() {
        String target = "nonExistentTarget";
        String enteredOtp = "1234";

        assertFalse(otpService.validateOtp(target, enteredOtp));
    }
}
