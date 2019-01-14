package urlshortener.team.web.SOAP;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import urlshortener.team.repository.QRRepository;
import urlshortener.team.repository.QRRepositoryImpl;


@Endpoint
public class QrEndpoint {

    private QRRepository qrRepository;

    @Autowired
    public QrEndpoint(QRRepository qrRepository) {
        this.qrRepository = qrRepository;
    }

    @PayloadRoot(namespace = "http://urlshortener.team/web/SOAP/schema", localPart = "getQRRequest")
    @ResponsePayload
    public GetQRResponse getQr(@RequestPayload GetQRRequest request) {
        GetQRResponse q = new GetQRResponse();
        q.setDone(true);
        qrRepository.createQR(request.getHash(),request.getUri());
        return q;
    }

}
