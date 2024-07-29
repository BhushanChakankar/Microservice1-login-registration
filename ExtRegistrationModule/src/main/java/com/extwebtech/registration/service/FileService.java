package com.extwebtech.registration.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
	/**
	 * Uploads a single file to the storage system.
	 *
	 * @param file The MultipartFile representing the file to be uploaded.
	 * @return A public URL of the uploaded file.
	 */
	String uploadFile(MultipartFile file);

	/**
	 * Uploads a list of files to the storage system.
	 *
	 * @param files The list of MultipartFile objects representing the files to be
	 *              uploaded.
	 * @return A list of public URLs corresponding to the uploaded files.
	 */
	List<String> uploadFiles(List<MultipartFile> files);

}