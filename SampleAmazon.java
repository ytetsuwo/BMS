import com.tetsuwo.amazon.Amazon;
import com.tetsuwo.amazon.Book;

public class SampleAmazon {
    public static void main(String args[]) {
	// Set your Amazon Key
	final String AccessKey = "";
	final String SecretKey = "";

	String isbn = args[0];
	Book book = Amazon.getInformation(isbn, AccessKey, SecretKey);
	System.out.println(book.getAuthor());
	System.out.println(book.getTitle());
	System.out.println(book.getPublicationDate());
	System.out.println(book.getURL());
    }
}
