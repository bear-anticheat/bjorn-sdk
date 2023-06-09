package al.hydro.bjorntest;

import com.bear.bjornsdk.BjornSDK;

public class BjornTest {

    public static void main(String[] args) {
        final BjornSDK bjornSDK = new BjornSDK("http://localhost:5000", "$2a$10$JczNDnyc3aGiOFGprxjKrulmBOIBrj6l/kvl7LYh0j0WYSmy/uVfG");

        bjornSDK.init();
    }
}
