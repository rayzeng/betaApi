package betaAPI.demo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class BetaAPIDemo {
    
    public void uploadGateway() throws IOException {
        NetUtil net = new NetUtil();
        Map<String, Object> map = new HashMap<>();
        map.put("app", "900007430");
        map.put("pid", 1);
        map.put("title", new String("梦幻西游".getBytes(), "UTF-8"));
        map.put("description", new String("非常diao的一款游戏， 你怕了吗".getBytes(), "UTF-8"));
        map.put("secret", 2);
        map.put("password", "1234");
        map.put("downloadLimit", 10);
        File file = new File("C:/Users/rayzeng/Desktop/pkg/梦幻西游.apk");
        String updateurl = "https://api.bugly.qq.com/beta/apiv1/exp/upload?appKey=BXEKfudgcgVDBb8k";
        HttpResponseBean bean =
                net.post(updateurl, null, map, file);
        String jsonstr = net.readBufferFromStream(bean.getInputStream());
        System.out.println(jsonstr);
    }
    
    public void getExpInfo() throws IOException {
        NetUtil net = new NetUtil();
        String updateurl = "https://api.bugly.qq.com/beta/apiv1/exp/86c11cda-09e6-4686-852f-1282cfa75402?app=900007430&pid=1&appKey=BXEKfudgcgVDBb8k";
        InputStream is = net.getHttpConnection(updateurl, "GET", null, "application/json", "");
        String jsonstr = net.readBufferFromStream(is);
        System.out.println(jsonstr);
    }
    
    public static void main(String[] args){
    	BetaAPIDemo test = new BetaAPIDemo();
    	try {
//			test.getExpInfo();
			test.uploadGateway();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
