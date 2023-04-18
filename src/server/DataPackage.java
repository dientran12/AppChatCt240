package server;

public class DataPackage {
	 private String message;
	    private byte[] imageData;

	    public DataPackage(String message, byte[] imageData) {
	        this.message = message;
	        this.imageData = imageData;
	    }

	    public String getMessage() {
	        return message;
	    }

	    public byte[] getImageData() {
	        return imageData;
	    }
}
