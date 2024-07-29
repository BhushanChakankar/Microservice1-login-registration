package com.extwebtech.registration.bean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class Base64MultipartFile implements MultipartFile {

	private final byte[] content;
	private final String name;
	private final String originalFilename;

	public Base64MultipartFile(byte[] content, String name) {
		this.content = content;
		this.name = StringUtils.hasText(name) ? name : "file";
		this.originalFilename = this.name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getOriginalFilename() {
		return originalFilename;
	}

	@Override
	public String getContentType() {
		return "application/octet-stream";
	}

	@Override
	public boolean isEmpty() {
		return content.length == 0;
	}

	@Override
	public long getSize() {
		return content.length;
	}

	@Override
	public byte[] getBytes() throws IOException {
		return content;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(content);
	}

	@Override
	public void transferTo(File dest) throws IOException, IllegalStateException {
	}
}
