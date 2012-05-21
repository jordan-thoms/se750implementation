package pdstore;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.Arrays;

public class Blob implements Serializable {
	private static final long serialVersionUID = -525731204528928408L;

	byte[] data;

	// references for lazy loading of Blob data
	RandomAccessFile file;
	long position;
	long length;
	
	
	public long getLength() {
		return length;
	}

	public byte[] getData() {
		if (data == null) {
			data = new byte[(int) length];
			try {
				synchronized (file) {
					file.seek(position);
					file.read(data);
				}
			} catch (IOException e) {
				throw new PDStoreException("Error while lazily loading blob.",
						e);
			}
		}
		return data;
	}

	public Blob() {
	}

	public Blob(RandomAccessFile file, long position, long length) {
		this.data = null;
		this.file = file;
		this.position = position;
		this.length = length;
	}

	public Blob(byte[] data) {
		this.data = data;
		this.length = data.length;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(getData());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Blob other = (Blob) obj;
		if (length != other.getLength())
			return false;
		if (!Arrays.equals(getData(), other.getData()))
			return false;
		return true;
	}
}
