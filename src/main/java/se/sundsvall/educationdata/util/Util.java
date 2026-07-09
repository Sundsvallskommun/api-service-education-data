package se.sundsvall.educationdata.util;

import se.sundsvall.dept44.util.jacoco.ExcludeFromJacocoGeneratedCoverageReport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@ExcludeFromJacocoGeneratedCoverageReport
public final class Util {

	private Util() {}

	public static byte[] zip(byte[] data) {
		var out = new ByteArrayOutputStream();
		try (var gz = new GZIPOutputStream(out)) {
			gz.write(data);
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to gzip json body", e);
		}
		return out.toByteArray();
	}

	public static byte[] unzip(byte[] data) {
		try (var gz = new GZIPInputStream(new ByteArrayInputStream(data))) {
			return gz.readAllBytes();
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to gunzip json body", e);
		}
	}
}
