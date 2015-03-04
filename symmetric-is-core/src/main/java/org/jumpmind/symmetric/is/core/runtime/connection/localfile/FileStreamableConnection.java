package org.jumpmind.symmetric.is.core.runtime.connection.localfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.jumpmind.exception.IoException;
import org.jumpmind.symmetric.is.core.model.Connection;

public class FileStreamableConnection implements IStreamableConnection {

	protected File file;
	
	public FileStreamableConnection(Connection connection, String basePath, boolean mustExist) {
		file = new File(basePath);
		if (!file.exists()) {
			if (!mustExist) {
				file.mkdirs();
			} else {
				throw new IoException("Could not find " + file.getAbsolutePath());
			}
		}
		//todo: get rid of basepath and must exist and get them from connection settings
	}
	
	@Override
	public void appendPath(String relativePath, boolean mustExist) {
		this.file = new File(file, relativePath);
		if (!file.exists()) {
			if (!mustExist) {
				file.getParentFile().mkdirs();
			} else {
				throw new IoException("Could not find " + file.getAbsolutePath());
			}
		}
	}
	
	@Override
	public void open() {
	}

	@Override
	public boolean requiresContentLength() {
		return false;
	}

	@Override
	public void setContentLength(int length) {
	}

	@Override
	public boolean supportsInputStream() {
		return file.exists();
	}

	@Override
	public InputStream getInputStream() {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new IoException(e);
        }
	}

	@Override
	public boolean supportsOutputStream() {
		return true;
	}

	@Override
	public OutputStream getOutputStream() {
        try {
            return new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new IoException(e);
        }
	}

	@Override
	public void close() {
		//TODO: should i close the output stream here or in the filewriter itself
	}

	@Override
	public boolean delete() {
        return FileUtils.deleteQuietly(file);
	}

	@Override
	public boolean supportsDelete() {
		return true;
	}
}
