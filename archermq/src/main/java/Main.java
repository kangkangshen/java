import io.netty.buffer.ByteBuf;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        List<ByteBuffer> list=new ArrayList<>(1024);
        while(true) {
            ByteBuffer buffer = ByteBuffer.allocateDirect(64*1024);
            list.add(buffer);
            Thread.sleep(10);

        }
    }
}
