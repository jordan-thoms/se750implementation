package pdintegrate;

import java.io.IOException;

import pdstore.GUID;

public class TestPDExtractionLayer {
	
	public static GUID PDModelID = new GUID("126e55a5bed011dfaaa4005056c00001");
	public static GUID PDTypeID = new GUID("12707880bed011dfaaa4005056c00001");
	
	static {
		PDDataMapper.registerType(TestPDExtractionLayer.class);
	}
	
	public GUID PDIdentity;
	
	public Integer i;
	static {
		PDDataMapper.registerRole(TestPDExtractionLayer.class, "i", new GUID());
	}
	
	public PDDataMapper loopback;
	static {
		PDDataMapper.registerRole(TestPDExtractionLayer.class, "loopback", new GUID());
	}
	
	public TestPDExtractionLayer () {
		this.PDIdentity = new GUID();
		
		this.i = 2;
		PDDataMapper.addLink(this, new GUID("12707880bed011dfaaa4005056c00001"), i);
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestPDExtractionLayer tpd = new TestPDExtractionLayer();
		tpd.i = 2;
		PDDataMapper.addLink(tpd, new GUID("12707880bed011dfaaa4005056c00001"), tpd.i);
		}
	}
