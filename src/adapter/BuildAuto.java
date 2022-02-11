package adapter;

public interface BuildAuto {
	public String buildAuto(String filename);

	public boolean printAuto(String automobileKey);

	public boolean serialize(String automobileKey, String fileName);

	public String deserialize(String fileName);
}