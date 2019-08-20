package httputil;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class SlackTests {
    private SlackHttpClient slackClient;
    private String channelName,channelId;

    @BeforeTest
    public void setUp() throws Exception
    {
        slackClient = new SlackHttpClient(10, -1L, "xoxp-732891013079-730559613780-731411000052-88fe9cc7f3ea5a37b40d75b592857f4b");
        channelName= "Lokesh" + new Random().nextInt(1000);
    }

    @Test(enabled = true, priority = 0)
    public void createTopic() throws Exception{
        String uri = "https://slack.com/api/channels.create";
        Map<String, String> payload = new HashMap<String, String>();
        payload.put("name", channelName);

        String response = slackClient.executePost(uri, payload);

        //String str="{\"ok\":true,\"channel\":{\"id\":\"CM5GMTW1Y\",\"name\":\"lokesh1\",\"is_channel\":true,\"created\":1566231260,\"is_archived\":false,\"is_general\":false,\"unlinked\":0,\"creator\":\"ULZURKBQA\",\"name_normalized\":\"lokesh1\",\"is_shared\":false,\"is_org_shared\":false,\"is_member\":true,\"is_private\":false,\"is_mpim\":false,\"last_read\":\"0000000000.000000\",\"latest\":null,\"unread_count\":0,\"unread_count_display\":0,\"members\":[\"ULZURKBQA\"],\"topic\":{\"value\":\"\",\"creator\":\"\",\"last_set\":0},\"purpose\":{\"value\":\"\",\"creator\":\"\",\"last_set\":0},\"previous_names\":[],\"priority\":0},\"warning\":\"missing_charset\",\"response_metadata\":{\"warnings\":[\"missing_charset\"]}}";

        JsonObject obj = new JsonParser().parse(response).getAsJsonObject();
        Assert.assertTrue("true".equalsIgnoreCase(obj.getAsJsonPrimitive("ok").getAsString()));
        channelId = obj.getAsJsonObject("channel").getAsJsonPrimitive("id").getAsString();
    }

    @Test(enabled = true, priority = 1, dependsOnMethods = "createTopic")
    public void joinChannel() throws Exception{
        String uri = "https://slack.com/api/channels.join";
        Map<String, String> payload = new HashMap<String, String>();
        payload.put("channel", channelId);

        String response = slackClient.executePost(uri, payload);
        System.out.println("JOIN : " + response);
        JsonObject obj = new JsonParser().parse(response).getAsJsonObject();
        Assert.assertTrue("true".equalsIgnoreCase(obj.getAsJsonPrimitive("ok").getAsString()));

    }

    @Test(enabled = true, priority = 2, dependsOnMethods = "createTopic")
    public void renameChannel() throws Exception{
        Map<String, String> payload = new HashMap<String, String>();
        payload.put("channel",channelId);
        payload.put("name", "RN" + channelName);
        String url = "https://slack.com/api/channels.rename";

        String response = slackClient.executePost(url, payload);
        System.out.println("RENAME : " + response);
        JsonObject obj = new JsonParser().parse(response).getAsJsonObject();
        Assert.assertTrue("true".equalsIgnoreCase(obj.getAsJsonPrimitive("ok").getAsString()));

    }

    @Test(enabled = true, priority = 3, dependsOnMethods = "createTopic")
    public void listChannel() throws Exception{
        String url = "https://slack.com/api/conversations.list";
        String response = slackClient.executeGet(url);
        System.out.println("List" + response);
    }

    @Test(enabled = true, priority = 4, dependsOnMethods = "createTopic")
    public void archiveChannel() throws Exception{
        Map<String, String> payload = new HashMap<String, String>();
        String url = "https://slack.com/api/channels.archive";
        payload.put("channel", channelId);

        String response = slackClient.executePost(url, payload);
        System.out.println("ARCHIVE : " + response);
        JsonObject obj = new JsonParser().parse(response).getAsJsonObject();
        Assert.assertTrue("true".equalsIgnoreCase(obj.getAsJsonPrimitive("ok").getAsString()));

        listChannel();

    }


}
