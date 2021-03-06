package de.dennisboldt.api;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;


/**
 * Unzips a given file
 *
 * @author Dennis Boldt
 * @see http://www.devx.com/getHelpOn/10MinuteSolution/20447
 */
public class Unzip {

	private Logger logger = Logger.getLogger(Unzip.class);

	public static final void copyInputStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int len;

		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);

		in.close();
		out.close();
	}

	public Unzip(File file, String destination) throws UnzipException {

		this.logger.info("Extracting " + file + " to " + destination);

		// TODO: Remove the warning
		@SuppressWarnings("rawtypes")
		Enumeration entries;
		ZipFile zipFile;

		if (!destination.endsWith("/")) {
			destination = destination + "/";
		}

		// Create the destination
		File f = new File(destination);
		if (f.exists()) {
			if (!f.isDirectory()) {
				throw new UnzipException("The destination is not a directory.");
			}
		} else {
			f.mkdir();
		}

		try {
			zipFile = new ZipFile(file);
			entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();

				if (entry.isDirectory()) {
					// Assume directories are stored parents first then
					// children.
					this.logger.info("Extracting directory: " + entry.getName());
					// This is not robust, just for demonstration purposes.
					(new File(destination + entry.getName())).mkdir();
					continue;
				}

				this.logger.info("Extracting file: " + entry.getName());
				copyInputStream(zipFile.getInputStream(entry),
						new BufferedOutputStream(new FileOutputStream(
								destination + entry.getName())));
			}

			zipFile.close();
		} catch (IOException ioe) {
			System.err.println("Unhandled exception:");
			ioe.printStackTrace();
			return;
		}
	}

}
