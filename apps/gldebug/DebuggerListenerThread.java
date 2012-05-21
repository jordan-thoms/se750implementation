package gldebug;
import java.lang.Thread;
import java.io.*;


class DebuggerListenerThread extends Thread
{
	protected BaseProtocol baseProtocol;
	protected DebuggerProtocol debuggerProtocol;
	protected boolean running;

	DebuggerListenerThread(DebuggerProtocol debuggerProtocol)
	{
		this.baseProtocol = debuggerProtocol.getBaseProtocol();
		this.debuggerProtocol = debuggerProtocol;
		running = true;
	}

	public void run()
	{
		try
		{
			while(running)
			{
				DebuggerProtocol.Response r = getResponse();
				if(r == null)
				{
					System.err.println("Null response");
					continue;
				}
				if(r.code == DebuggerProtocol.RESP_ERROR)
					System.err.println(((DebuggerProtocol.ResponseError)r).error);
				debuggerProtocol.processResponse(r);
			}
		}
		catch(IOException e)
		{
			// It's cool TODO: handle this in a potentially better way
		}
	}
	
	public void cease()
	{
		running = false;
	}
	
	DebuggerProtocol.Response getResponse() throws IOException
	{
		int code, id;

		code = baseProtocol.recieveCode();
		id = baseProtocol.recieveCode();

		switch (code)
		{
		case DebuggerProtocol.RESP_ANS: return debuggerProtocol.getResponseAns(code, id);
		case DebuggerProtocol.RESP_BREAK: return debuggerProtocol.getResponseBreak(code, id);
		case DebuggerProtocol.RESP_STOP: /* Obsolete alias of RESP_BREAK */
			return debuggerProtocol.getResponseBreak(debuggerProtocol.RESP_BREAK, id);
		case DebuggerProtocol.RESP_BREAK_EVENT: return debuggerProtocol.getResponseBreakEvent(code, id);
		case DebuggerProtocol.RESP_STATE: return debuggerProtocol.getResponseState(code, id);
		case DebuggerProtocol.RESP_ERROR: return debuggerProtocol.getResponseError(code, id);
		case DebuggerProtocol.RESP_RUNNING: return debuggerProtocol.getResponseRunning(code, id);
		case DebuggerProtocol.RESP_SCREENSHOT: return debuggerProtocol.getResponseScreenshot(code, id);
		case DebuggerProtocol.RESP_STATE_HEADER: return debuggerProtocol.getResponseStateTree(code, id);
		//case DebuggerProtocol.RESP_STATE_NODE_BEGIN: return debuggerProtocol.getResponseStateTree(code, id); // Old TODO: update or remove
		case DebuggerProtocol.RESP_CALL: return debuggerProtocol.getResponseCall(code, id);
		case DebuggerProtocol.RESP_DATA: return debuggerProtocol.getResponseData(code, id); // TODO: IMPLEMENT LATER
		default:
			System.err.println("Unexpected response" + code + "\n");
			return null;
		}
	}
}
