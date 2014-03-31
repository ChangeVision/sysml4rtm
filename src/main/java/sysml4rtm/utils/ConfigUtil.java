package sysml4rtm.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

public class ConfigUtil {
	public static final String FILE_NAME = "sysml4simulink.properties";
	public static final String DEFAULT_CODE_OUTPUT_DIR_KEY = "default_code_output_directory";
	public static final String OPEN_GENERATED_FOLDER_KEY = "open_generated_folder";
	
	private static final String FILE_PATH;
	private static Properties config;

	static {
		config = new Properties();

		StringBuilder builder = new StringBuilder();
		builder.append(System.getProperty("user.home"));
		builder.append(File.separator);
		builder.append(".astah");
		builder.append(File.separator);
		builder.append("hrtm");
		builder.append(File.separator);
		builder.append(FILE_NAME);

		FILE_PATH = builder.toString();

		load();
	}

	public static boolean isOpenGeneratedFolderAutomatically(){
		String value = config.getProperty(OPEN_GENERATED_FOLDER_KEY);
		return Boolean.parseBoolean(value);
	}
	
	public static void saveOpenGeneratedFolderAutomaticcaly(boolean value){
		config.put(OPEN_GENERATED_FOLDER_KEY, String.valueOf(value));
		store();
	}
	
	public static String getDefaultCodeOutputDirectoryPath() {
		String outputDirPath = config.getProperty(DEFAULT_CODE_OUTPUT_DIR_KEY);
		if (StringUtils.isBlank(outputDirPath)) {
			outputDirPath = SystemUtils.getUserHome().getAbsolutePath();
		}
		return outputDirPath;
	}

	public static void saveCodeOutputPath(String path) {
		config.put(DEFAULT_CODE_OUTPUT_DIR_KEY, path);
		store();
	}

	public static void load() {
		InputStream stream = null;
		try {
			stream = new FileInputStream(FILE_PATH);
			config.load(stream);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void store() {
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(FILE_PATH);
			config.store(stream, null);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
