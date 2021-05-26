import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class Testtest {

	@Test
	public void test() {
		GUI test1 = new GUI("localhost", 1234);
		Clients test = new Clients("localhost", 1234, "dad", test1);
		
		
	}

}
