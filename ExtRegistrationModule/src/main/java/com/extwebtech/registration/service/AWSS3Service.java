package com.extwebtech.registration.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

/**
 * Service class for handling file-related operations using AWS S3.
 */
@Service
public class AWSS3Service implements FileService {

	@Autowired
	private AmazonS3 amazonS3;

	@Value("${AWS_BUCKET}")
	private String bucketName;

	/**
	 * Uploads a single file to AWS S3.
	 *
	 * @param file The file to be uploaded
	 * @return The public URL of the uploaded file
	 */
	@Override
	public String uploadFile(MultipartFile file) {
		if (file != null) {
			String filenameExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
			String key = UUID.randomUUID().toString() + "." + filenameExtension;
			ObjectMetadata metaData = new ObjectMetadata();
			metaData.setContentLength(file.getSize());
			metaData.setContentType(file.getContentType());
			try {
				amazonS3.putObject(bucketName, key, file.getInputStream(), metaData);
			} catch (IOException e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
						"An exception occurred while uploading the file");
			}
			String publicUrl = amazonS3.getUrl(bucketName, key).toString();
			return publicUrl;
		} else {
			return null;
		}
	}

	/**
	 * Uploads multiple files to AWS S3.
	 *
	 * @param files The list of files to be uploaded
	 * @return The list of public URLs of the uploaded files
	 */
	@Override
	public List<String> uploadFiles(List<MultipartFile> files) {
		List<String> publicUrls = new ArrayList<>();
		for (MultipartFile file : files) {
			publicUrls.add(uploadFile(file));
		}
		return publicUrls;
	}
}
