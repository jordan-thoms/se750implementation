package benchmark.serializer;

public interface Serializer {
	
	public void gatherData(ObjectBundle bundle, boolean requiresPDstoreTripleFormat);
	
	public void serialize();
	
	public Long triplesGenerated();
}
