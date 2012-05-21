package pdstore;

public interface OperationI {
	public Object apply(PDStore store, GUID transaction, Object parameter);
}
