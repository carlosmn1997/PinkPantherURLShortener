package urlshortener.team.domain;

import com.google.common.hash.Hashing;
import org.springframework.http.HttpStatus;
import urlshortener.team.web.UrlShortenerController;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class ShortURL {

	private String hash;
	private String target;
	private URI uri;
	private String sponsor;
	private Date created;
	private String owner;
	private Integer mode;
	private Boolean safe;
	private String ip;
	private String country;
	private Boolean checkStatus;
	private Boolean aliveOnLastCheck;
	private Boolean qr;
	private byte[] qrImage;

	/*
	 * ShortURL's constructor
	 */
	public ShortURL(String target, String sponsor, String ip, Boolean checkStatus, Boolean qr) {
		this.hash = Hashing.murmur3_32().hashString(target + sponsor + ip
				+ checkStatus.toString() + qr.toString(), StandardCharsets.UTF_8).toString();
		this.target = target;
		this.uri = linkTo(methodOn(UrlShortenerController.class)
				.redirectTo(hash, null)).toUri();
		this.sponsor = sponsor;
		this.created = new Date(System.currentTimeMillis());
		this.owner = UUID.randomUUID().toString();
		this.mode = HttpStatus.TEMPORARY_REDIRECT.value();
		this.safe = true;
		this.ip = ip;
		this.country = null;
		this.checkStatus = checkStatus;
		this.aliveOnLastCheck = false;
		this.qr = qr;
	}

	public ShortURL(String hash, String target, URI uri, String sponsor,
					Date created, String owner, Integer mode, Boolean safe,
					String ip, String country, Boolean checkStatus, Boolean aliveOnLastCheck) {
		this.hash = hash;
		this.target = target;
		this.uri = uri;
		this.sponsor = sponsor;
		this.created = created;
		this.owner = owner;
		this.mode = mode;
		this.safe = safe;
		this.ip = ip;
		this.country = country;
		this.checkStatus = checkStatus;
		this.aliveOnLastCheck = aliveOnLastCheck;
	}

	public ShortURL(String hash, String target, URI uri, String sponsor,
					Date created, String owner, Integer mode, Boolean safe,
					String ip, String country, Boolean checkStatus, Boolean aliveOnLastCheck,
					Boolean qr, byte[] qrImage) {
		this.hash = hash;
		this.target = target;
		this.uri = uri;
		this.sponsor = sponsor;
		this.created = created;
		this.owner = owner;
		this.mode = mode;
		this.safe = safe;
		this.ip = ip;
		this.country = country;
		this.checkStatus = checkStatus;
		this.aliveOnLastCheck = aliveOnLastCheck;
		this.qr=qr;
		this.qrImage=qrImage;
	}

	public ShortURL() {}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public String getSponsor() {
		return sponsor;
	}

	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Integer getMode() {
		return mode;
	}

	public void setMode(Integer mode) {
		this.mode = mode;
	}

	public Boolean getSafe() {
		return safe;
	}

	public void setSafe(Boolean safe) {
		this.safe = safe;
	}

	public String getIP() {
		return ip;
	}

	public void setIP(String ip) {
		this.ip = ip;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Boolean isCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(Boolean checkStatus) {
		this.checkStatus = checkStatus;
	}

	public Boolean getQR() {
		return qr;
	}

	public void setQr(Boolean qr) {
		this.qr = qr;
	}

	public byte[] getQRimage() {
		return qrImage;
	}

	public void setQrImage(byte[] qrImage) {
		this.qrImage = qrImage;
	}

	public Boolean isAliveOnLastCheck(){ return this.aliveOnLastCheck;}

	public void setAliveOnLastCheck(boolean b){ this.aliveOnLastCheck = b; }
}
