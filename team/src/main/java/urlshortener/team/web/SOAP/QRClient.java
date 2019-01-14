package urlshortener.team.web.SOAP;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.ClassUtils;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import java.util.concurrent.TimeUnit;


public class QRClient extends WebServiceGatewaySupport {

    private static final Logger log = LoggerFactory.getLogger(QRClient.class);

    private Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

    public QRClient(){
        marshaller.setPackagesToScan(ClassUtils.getPackageName(GetQRRequest.class));
        try {
            marshaller.afterPropertiesSet();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public GetQRResponse getQR(String hash,String uri) {

        GetQRRequest request = new GetQRRequest();
        request.setHash(hash);
        request.setUri(uri);

        log.info("Requesting QR for " + uri);

        WebServiceTemplate ws = new WebServiceTemplate(marshaller);

        GetQRResponse qr = new GetQRResponse();
        try {

            Object response = ws.marshalSendAndReceive("http://localhost:8080/ws", request);
            qr = (GetQRResponse) response;

        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Segundo");
        return qr;
    }

}
