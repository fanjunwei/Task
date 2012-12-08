package com.baoxue.task.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Zip {
	static final int BUFFER = 8192;

	public static String zipBase64compress(byte[] buf) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ZipOutputStream zipout = new ZipOutputStream(out);
			ByteArrayInputStream bis = new ByteArrayInputStream(buf);
			ZipEntry entry = new ZipEntry("data");
			zipout.putNextEntry(entry);

			int count;
			byte data[] = new byte[BUFFER];
			while ((count = bis.read(data, 0, BUFFER)) != -1) {
				zipout.write(data, 0, count);
			}
			bis.close();
			zipout.close();

			return Base64.encode(out.toByteArray());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static byte[] zipBase64decompression(String base64) {
		try {
			byte[] array = Base64.decode(base64);
			ByteArrayInputStream input = new ByteArrayInputStream(array);

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			ZipInputStream zipInput = new ZipInputStream(input);
			zipInput.getNextEntry();
			int count;
			byte data[] = new byte[BUFFER];
			while ((count = zipInput.read(data, 0, BUFFER)) != -1) {
				out.write(data, 0, count);
			}
			zipInput.close();
			out.close();
			return out.toByteArray();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}