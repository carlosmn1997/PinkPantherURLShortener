package urlshortener.team.web.SOAP;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import urlshortener.team.repository.QRRepository;



public class QrEndpoint {

    private QRRepository qrRepository;

    @Autowired
    public QrEndpoint(QRRepository qrRepository) {
        this.qrRepository = qrRepository;
    }

    @PayloadRoot(namespace = "http://qr/web/ws/schema", localPart = "GetQRRequest")
    @ResponsePayload
    public GetQRResponse getQr(@RequestPayload GetQRRequest request) {
        GetQRResponse q = new GetQRResponse();
        System.out.println("llego");
        q.setDone(qrRepository.createQR(request.getHash(),request.getUri()));
        return q;
    }

}
