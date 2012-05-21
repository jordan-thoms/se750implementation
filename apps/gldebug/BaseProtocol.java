package gldebug;
import java.io.*;
import java.net.*;

class BaseProtocol
{
	protected Socket socket = null;
	
	public BaseProtocol(Socket sock)
	{
		try
		{
			socket = sock;
		}
		catch(Exception e)
		{
			System.err.println(e);
			System.exit(-1);
		}
	}
	
	public void sendCode(int code)
	{
		try
		{
			DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
			outStream.writeInt(code);
		} 
		catch(IOException e)
		{
			System.err.println(e);
			System.exit(-1);
		}
	}
	
	public void sendString(String string)
	{
		try // TODO: need to check that this works, otherwise send the string one char at a time
		{
			DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
			byte[] stringBytes = string.getBytes();
			outStream.writeInt(stringBytes.length);
			for(int i = 0; i < stringBytes.length; ++i)
			{
				outStream.write(stringBytes[i]);
			}
		} 
		catch(IOException e)
		{
			System.err.println(e);
			System.exit(-1);
		}
	}
	
	public int recieveCode() throws IOException
	{
		try
		{
			DataInputStream inStream = new DataInputStream(socket.getInputStream());
			return inStream.readInt();
		} 
		catch(IOException e)
		{
			System.err.println(e);
			throw e;
		}
	}
	
	public long recieveLong() throws IOException
	{
		try
		{
			DataInputStream inStream = new DataInputStream(socket.getInputStream());
			return inStream.readLong();
		} 
		catch(IOException e)
		{
			System.err.println(e);
			throw e;
		}
	}
	
	public byte[] recieveBytes() throws IOException
	{
		Integer dummyInt = new Integer(0);
		
		return recieveBytes(dummyInt);
	}
	
	public byte[] recieveBytes(Integer length) throws IOException
	{
		try
		{
			DataInputStream inStream = new DataInputStream(socket.getInputStream());
			length = inStream.readInt();
			byte[] bytes = new byte[length];
			for(int i = 0; i < length; ++i)
			{
				bytes[i] = inStream.readByte();
			}
			return bytes;
		}
		catch(IOException e)
		{
			System.err.println(e);
			throw e;
		}	
	}
	
	public String recieveString() throws IOException
	{
		Integer dummyInt = new Integer(0);
	
		return recieveString(dummyInt);
	}
	
	public String recieveString(Integer length) throws IOException
	{
		try
		{
			DataInputStream inStream = new DataInputStream(socket.getInputStream());
			length = inStream.readInt();
			byte[] stringBytes = new byte[length];
			for(int i = 0; i < length; ++i)
			{
				stringBytes[i] = inStream.readByte();
			}
			return new String(stringBytes, "US-ASCII");
		} 
		catch(IOException e)
		{
			System.err.println(e);
			throw e;
		}
	}
}