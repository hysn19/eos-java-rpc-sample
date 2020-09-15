package eosio.java;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import client.EosApiClientFactory;
import client.EosApiRestClient;
import client.domain.common.transaction.PackedTransaction;
import client.domain.common.transaction.SignedPackedTransaction;
import client.domain.common.transaction.TransactionAction;
import client.domain.common.transaction.TransactionAuthorization;
import client.domain.response.chain.Block;
import client.domain.response.chain.ChainInfo;
import client.domain.response.chain.transaction.PushedTransaction;

/**
 * Hello world!
 *
 */
public class App 
{

    private static String walletBaseUrl = "http://127.0.0.1:8899";
    private static String chainBaseUrl = "http://127.0.0.1:8888";
    private static String historyBaseUrl = "http://127.0.0.1:8888";

    /* Create the rest client */
    private static EosApiRestClient eosApiRestClient = EosApiClientFactory
            .newInstance(walletBaseUrl, chainBaseUrl, historyBaseUrl).newRestClient();

    /* Get the head block */
    private static ChainInfo chainInfo = eosApiRestClient.getChainInfo();
    private static Block block = eosApiRestClient.getBlock(eosApiRestClient.getChainInfo().getHeadBlockId());

    public static void main(final String[] args) {
        System.out.println("hello, world");
        // pushtransaction();
        readtransaction("236e90f1a1e365c8d7132f7995f27e67a2df23d633c83c9b830b0ef42cdb5503");
    }

    public static void readtransaction(String hash) {
        
        String rpc_url = "http://localhost:8888/v1/chain/get_table_rows";
        String payload =
                // "{\"code\": \"hello\",\"table\": \"kvmng\",\"scope\": \"hello\",\"index_position\": \"1\",\"key_type\": \"string\",\"encode_type\": \"string\",\"upper_bound\": \"string\",\"lower_bound\": \"string\"}";
                "{\"json\": true,\"code\": \"hello\",\"table\": \"kvmng\",\"scope\": \"hello\",\"index_position\": \"secondary\",\"key_type\": \"sha256\",\"upper_bound\":" + hash + ",\"lower_bound\":" + hash + "}";

        try {
                URL url = new URL(rpc_url);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type","application/json");
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(payload);
                wr.flush();
                wr.close();
                
                int responseCode = con.getResponseCode();
                System.out.println("Response Code : " + responseCode);
                
                BufferedReader iny = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String output;
                StringBuffer response = new StringBuffer();

                while ((output = iny.readLine()) != null) {
                        response.append(output);
                }
                iny.close();

                //printing result from response
                //System.out.println(response.toString());

                JSONParser parser = new JSONParser();
                Object obj = parser.parse( response.toString() );
                JSONObject jsonObj = (JSONObject) obj;

                JSONArray rows = (JSONArray) jsonObj.get("rows");
                JSONObject row = (JSONObject) rows.get(0);
                
                String value = (String) row.get("value");

                //printing value from row
                System.out.println("value: " + value);

        } catch(Exception e) {
                e.printStackTrace();
        }
    }

    public static void pushtransaction() {

        /* Create the json array of arguments */
        // Map<String, String> map = new HashMap<>(4);
        // map.put("from", "eosio");
        // map.put("to", "jckim");
        // map.put("quantity", "1.0000 SYS");
        // map.put("memo", "");
        // AbiJsonToBin data = eosApiRestClient.abiJsonToBin("eosio.token", "transfer",
        // map);

        /* Create Transaction Action Authorization */
        final TransactionAuthorization transactionAuthorization = new TransactionAuthorization();
        transactionAuthorization.setActor("hello");
        transactionAuthorization.setPermission("active");

        /* Create Transaction Action */
        final TransactionAction transactionAction1 = new TransactionAction();
        transactionAction1.setAccount("eosio");
        transactionAction1.setName("setcode");
        transactionAction1.setData(
                "00000000001aa36a0000f8070061736d0100000001370b6000017f60027f7f0060037f7f7f017f60027f7f017f60017f0060017e0060027f7e0060000060037e7e7e0060017f017f60027e7e00028e010803656e7610616374696f6e5f646174615f73697a65000003656e760c656f73696f5f617373657274000103656e76066d656d736574000203656e7610726561645f616374696f6e5f64617461000303656e76066d656d637079000203656e76067072696e7473000403656e76067072696e746e000503656e7611656f73696f5f6173736572745f636f6465000603080707080907040a0a0405017001010105030100010616037f014180c0000b7f0041bdc0000b7f0041bdc0000b070901056170706c7900090a9c05070400100b0b9201001008200020015104404280808080808080c0eb00200251044020002001100d054280808080808080c2eb00200251044020002001100e052000428080808080c0ba98d500520440410042808080d9d3b3ed82ef0010070b0b0b05428080808080c0ba98d50020015104404280808080aefadeeaa47f2002510440410042818080d9d3b3ed82ef0010070b0b0b4100100c0b800101037f02400240024002402000450d004100410028028c40200041107622016a220236028c404100410028028440220320006a41076a417871220036028440200241107420004d0d0120014000417f460d020c030b41000f0b4100200241016a36028c40200141016a4000417f470d010b41004190c000100120030f0b20030b3601017f230041106b2200410036020c4100200028020c28020041076a417871220036028440410020003602804041003f0036028c400b02000ba10102047f017e230041106b22022103200224000240024002400240024010002204450d002004418004490d012004100a21020c020b2003420037030841002102200341086a21050c020b20022004410f6a4170716b220224000b2002200410031a20034200370308200341086a2105200441074b0d010b410041b8c00010010b20052002410810041a2003290308210641a9c000100520061006200341106a24000ba10102047f017e230041106b22022103200224000240024002400240024010002204450d002004418004490d012004100a21020c020b2003420037030841002102200341086a21050c020b20022004410f6a4170716b220224000b2002200410031a20034200370308200341086a2105200441074b0d010b410041b8c00010010b20052002410810041a2003290308210641b1c000100520061006200341106a24000b0b4c04004190c0000b216661696c656420746f20616c6c6f636174652070616765730068656c6c6f2c20000041b1c0000b0768656c6c6f32000041b8c0000b0572656164000041000b0440200000");
        // transactionAction1.setData(data.getBinargs());
        transactionAction1.setAuthorization(Collections.singletonList(transactionAuthorization));

        final TransactionAction transactionAction2 = new TransactionAction();
        transactionAction2.setAccount("eosio");
        transactionAction2.setName("setabi");
        transactionAction2.setData(
                "00000000001aa36a4f0e656f73696f3a3a6162692f312e31000202686900010475736572046e616d650368693200010475736572046e616d6502000000000000806b02686900000000000000846b03686932000000000000");
        // transactionAction2.setData(data.getBinargs());
        transactionAction2.setAuthorization(Collections.singletonList(transactionAuthorization));

        final String expiration = ZonedDateTime.now(ZoneId.of("GMT")).plusMinutes(3).truncatedTo(ChronoUnit.SECONDS)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        /* Create a transaction */
        final PackedTransaction packedTransaction = new PackedTransaction();
        packedTransaction.setExpiration(expiration);
        packedTransaction.setRefBlockNum(block.getBlockNum().toString());
        packedTransaction.setRefBlockPrefix(block.getRefBlockPrefix().toString());
        packedTransaction.setMax_net_usage_words("0");
        packedTransaction.setRegion("0");
        // packedTransaction.setActions(Collections.singletonList(transactionAction));

        final List<TransactionAction> actions = new ArrayList<TransactionAction>();
        actions.add(transactionAction1);
        actions.add(transactionAction2);

        packedTransaction.setActions(actions);

        /* Sign the Transaction */
        final SignedPackedTransaction signedPackedTransaction = eosApiRestClient.signTransaction(packedTransaction,
                Collections.singletonList("EOS6MRyAjQq8ud7hVNYcfnVPJqcVpscN5So8BhtHuGYqET5GDW5CV"),
                chainInfo.getChainId());

        /* Push the transaction */
        final PushedTransaction pushedTransaction = eosApiRestClient.pushTransaction("none", signedPackedTransaction);
        System.out.println(pushedTransaction.toString());
    }
}
