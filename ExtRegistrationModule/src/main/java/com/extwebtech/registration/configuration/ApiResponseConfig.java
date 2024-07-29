package com.extwebtech.registration.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Data;

@Configuration
@Component
@Data
public class ApiResponseConfig {

	@Value("${api.response.access-denied.message}")
	private String accessDeniedMessage;

	@Value("${please.provide-token}")
	private String pleaseProvideToken;;

	@Value("${app.response.success.status}")
	private boolean successResponseStatus;

	@Value("${app.response.success.statusCode}")
	private String successResponseStatusCode;

	@Value("${app.response.success.message}")
	private String successResponseMessage;

	@Value("${app.response.error.status}")
	private boolean errorResponseStatus;

	@Value("${app.response.error.statusCode}")
	private String errorResponseStatusCode;

	@Value("${app.response.error.message}")
	private String errorResponseMessage;

	@Value("${app.response.notFound.status}")
	private boolean notFoundStatus;

	@Value("${app.response.notFound.statusCode}")
	private String notFoundStatusCode;

	@Value("${app.response.notFound.message}")
	private String notFoundMessage;

	@Value("${app.response.validationFailure.status}")
	private boolean validationFailureStatus;

	@Value("${app.response.validationFailure.statusCode}")
	private String validationFailureStatusCode;

	@Value("${app.response.validationFailure.message}")
	private String validationFailureMessage;

	@Value("${app.response.badRequest.status}")
	private boolean badRequestStatus;

	@Value("${app.response.badRequest.statusCode}")
	private String badRequestStatusCode;

	@Value("${app.response.badRequest.message}")
	private String badRequestMessage;

	@Value("${app.response.unauthorized.status}")
	private boolean unauthorizedStatus;

	@Value("${app.response.unauthorized.statusCode}")
	private int unauthorizedStatusCode;

	@Value("${app.response.unauthorized.message}")
	private String unauthorizedMessage;

	@Value("${app.response.forbidden.status}")
	private boolean forbiddenStatus;

	@Value("${app.response.forbidden.statusCode}")
	private int forbiddenStatusCode;

	@Value("${app.response.forbidden.message}")
	private String forbiddenMessage;

	@Value("${api.response.login.mobile-role-missing}")
	private String loginMobileRoleMissingMessage;

	@Value("${api.response.login.invalid-credentials}")
	private String loginInvalidCredentialsMessage;

	@Value("${api.response.login.otp-sent}")
	private String loginOtpSentMessageTemplate;

	@Value("${api.response.validate-otp.invalid-otp}")
	private String validateOtpInvalidOtpMessage;

	@Value("${api.response.validate-otp}")
	private String validatedOtp;

	@Value("${app.response.error.duplicateMobileNumber}")
	private String duplicateMobileNumberErrorMessage;

	@Value("${unsupported.role.message}")
	private String unsupportedRoleMessage;

	@Value("${unsupported.entity.message}")
	private String unsupportedEntityMessage;

	@Value("${registration.message}")
	private String registrationMessage;

	@Value("${s3.upload.error.message}")
	private String s3UploadErrorMessage;

	@Value("${message.locationEnabledSuccess}")
	private String locationEnabledSuccessMessage;

	@Value("${message.userNotFound}")
	private String userNotFoundMessage;

	@Value("${message.errorOccurred}")
	private String errorOccurredMessage;

	@Value("${message.profilePhotoUpdateSuccess}")
	private String profilePhotoUpdateSuccessMessage;

	@Value("${message.profilePhotoUpdateError}")
	private String profilePhotoUpdateErrorMessage;

	@Value("${message.profilePhotoSaveSuccess}")
	private String profilePhotoSaveSuccessMessage;

	@Value("${message.profilePhotoSaveError}")
	private String profilePhotoSaveErrorMessage;

	@Value("${message.profilePhotoDeleteSuccess}")
	private String profilePhotoDeleteSuccessMessage;

	@Value("${message.profilePhotoDeleteError}")
	private String profilePhotoDeleteErrorMessage;

	@Value("${successRatingMessage}")
	private String successRatingMessage;

	@Value("${failureRatingMessage}")
	private String failureRatingMessage;

	@Value("${successReviewMessage}")
	private String successReviewMessage;

	@Value("${failureReviewMessage}")
	private String failureReviewMessage;

	@Value("${success.cancelOrder}")
	private String successCancelOrder;

	@Value("${error.invalidOrderStatus}")
	private String errorInvalidOrderStatus;

	@Value("${error.orderNotFound}")
	private String errorOrderNotFound;

	@Value("${cart.not.found.message}")
	private String cartNotFoundMessage;

	@Value("${registration.customer.success}")
	private String customerRegistrationSuccess;

	@Value("${registration.seller.success}")
	private String sellerRegistrationSuccess;

	@Value("${registration.admin.success}")
	private String adminRegistrationSuccess;

	@Value("${registration.customer.error}")
	private String customerRegistrationError;

	@Value("${registration.seller.error}")
	private String sellerRegistrationError;

	@Value("${registration.admin.error}")
	private String adminRegistrationError;

	@Value("${error.duplicateUser}")
	private String duplicateUser;

	@Value("${twilio.to-number}")
	private String smsTOMobile;

	@Value("${module.address}")
	private String addressModule;

	@Value("${module.registration}")
	private String registrationModule;

	@Value("${module.login}")
	private String loginModule;

	@Value("${module.subscriptions}")
	private String subscriptionsModule;

	@Value("${module.operation.create}")
	private String createOperation;

	@Value("${module.operation.read}")
	private String readOperation;

	@Value("${module.operation.update}")
	private String updateOperation;

	@Value("${module.operation.delete}")
	private String deleteOperation;

	@Value("${subscription.expired.weekly}")
	private String subscriptionExpiredWeekly;

	@Value("${subscription.expired.monthly}")
	private String subscriptionExpiredMonthly;

	@Value("${subscription.expired.yearly}")
	private String subscriptionExpiredYearly;

	@Value("${error.messages.invalidEmail}")
	private String invalidEmail;

	@Value("${error.messages.referralExists}")
	private String referralExists;

	@Value("${error.messages.referralEmailSendingFailed}")
	private String referralEmailSendingFailed;

	@Value("${success.messages.referralEmailSent}")
	private String referralEmailSent;

	@Value("${email.subject}")
	private String emailSubject;

	@Value("${email.text}")
	private String emailText;

	@Value("${database.users.table}")
	private String usersTable;

	@Value("${database.ratings_reviews.table}")
	private String ratingsReviewsTable;

	@Value("${database.users.user_id}")
	private String userIdColumn;

	@Value("${database.users.name}")
	private String nameColumn;

	@Value("${database.users.mobile}")
	private String mobileColumn;

	@Value("${database.users.email}")
	private String emailColumn;

	@Value("${database.users.profile_photo}")
	private String profilePhotoColumn;

	@Value("${database.users.business_name}")
	private String businessNameColumn;

	@Value("${database.users.owner_name}")
	private String ownerNameColumn;

	@Value("${database.users.gst}")
	private String gstColumn;

	@Value("${database.users.account_holder_name}")
	private String accountHolderNameColumn;

	@Value("${database.users.account_name}")
	private String accountNameColumn;

	@Value("${database.users.ifsc_code}")
	private String ifscCodeColumn;

	@Value("${database.users.bank_upi}")
	private String bankUpiColumn;

	@Value("${database.users.role_id}")
	private String roleIdColumn;

	@Value("${database.users.language_id}")
	private String languageIdColumn;

	@Value("${database.users.created_date}")
	private String createdDateColumn;

	@Value("${database.users.updated_date}")
	private String updatedDateColumn;

	@Value("${database.users.country_id}")
	private String countryIdColumn;

	@Value("${database.users.business_address}")
	private String businessAddressColumn;

	@Value("${database.users.extra_field}")
	private String extraFieldColumn;

	@Value("${database.users.official_documents}")
	private String officialDocumentsColumn;

	@Value("${database.users.active}")
	private String activeColumn;

	@Value("${database.users.subscription_id}")
	private String subscriptionIdColumn;

	@Value("${database.users.subscription_start_date}")
	private String subscriptionStartDateColumn;

	@Value("${database.users.subscription_days_remaining}")
	private String subscriptionDaysRemainingColumn;

	@Value("${database.ratings_reviews.ratings}")
	private String ratingsColumn;

	@Value("${database.ratings_reviews.reviews}")
	private String reviewsColumn;

	@Value("${database.ratings_reviews.customer_id}")
	private String customerIdColumn;

	@Value("${database.ratings_reviews.seller_id}")
	private String sellerIdColumn;

	@Value("${database.notifications.table}")
	private String notificationsTable;

	@Value("${database.login_details.table}")
	private String loginDetailsTable;

	@Value("${database.admin_notification.table}")
	private String adminNotificationTable;

	@Value("${database.notifications.user_id}")
	private String userIdColumnNotify;

	@Value("${database.notifications.notification_id}")
	private String notificationIdColumn;

	@Value("${database.notifications.created_by}")
	private String createdByColumn;

	@Value("${database.login_details.device_token}")
	private String deviceTokenColumn;

	@Value("${database.admin_notification.title}")
	private String titleColumn;

	@Value("${database.admin_notification.description}")
	private String descriptionColumn;

	@Value("${database.admin_notification.id}")
	private String idColumn;

	@Value("${database.admin_notification.created_by}")
	private String adminNotificationCreatedByColumn;

	@Value("${database.subscriptions.id}")
	private String subscriptionsIdColumn;

	@Value("${database.login_details.user_mobile}")
	private String userMobileColumn;

	@Value("${database.login_details.device_details}")
	private String deviceDetailsColumn;

	@Value("${database.login_details.device_type}")
	private String deviceTypeColumn;

	@Value("${database.login_details.user_id}")
	private String userIdLoginDetailsColumn;

	@Value("${log.messages.methodStart}")
	private String methodStartMessage;

	@Value("${log.messages.methodEnd}")
	private String methodEndMessage;

	@Value("${log.messages.methodError}")
	private String methodErrorMessage;

	@Value("${log.messages.methodData}")
	private String methodDataMessage;

	@Value("${api.otp.expire.time}")
	private int otpExpireTime;

	@Value("${api.otp.expire.message}")
	private String otpExpireMessage;

	@Value("${api.otp.expire.status.code}")
	private String otpExpireStatusCode;
}