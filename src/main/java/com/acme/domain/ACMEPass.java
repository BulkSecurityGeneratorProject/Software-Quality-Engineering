package com.acme.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.xml.bind.DatatypeConverter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A ACMEPass.
 */
@Entity
@Table(name = "acmepass")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ACMEPass extends AbstractDatedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@Size(min = 3)
	@Column(name = "site", nullable = false)
	private String site;

	@NotNull
	@Column(name = "login", nullable = false)
	private String login;

	@NotNull
	@Column(name = "password", nullable = false)
	private String password;

	@ManyToOne
	@NotNull
	private User user;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSite() {
		return site;
	}

	public ACMEPass site(String site) {
		this.site = site;
		return this;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getLogin() {
		return login;
	}

	public ACMEPass login(String login) {
		this.login = login;
		return this;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		if (password != null) {
			try {
				Key k = this.getKey();
				Cipher c = Cipher.getInstance("AES");
				c.init(Cipher.DECRYPT_MODE, k);
				return new String(c.doFinal(DatatypeConverter.parseBase64Binary(password)));
			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
				Logger.getLogger(ACMEPass.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		return null;
	}

	public ACMEPass password(String password) {
		setPassword(password);
		return this;
	}

	public void setPassword(String password) {
		try {
			Key k = this.getKey();
			Cipher c = Cipher.getInstance("AES");
			c.init(Cipher.ENCRYPT_MODE, k);
			this.password = DatatypeConverter.printBase64Binary(c.doFinal(password.getBytes()));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
			Logger.getLogger(ACMEPass.class.getName()).log(Level.SEVERE, null, ex);
			this.password = null;
		}
	}

	public User getUser() {
		return user;
	}

	public ACMEPass user(User user) {
		this.user = user;
		return this;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ACMEPass acmePass = (ACMEPass) o;
		if (acmePass.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, acmePass.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "ACMEPass{"
			+ "id=" + id
			+ ", site='" + site + "'"
			+ ", login='" + login + "'"
			+ ", password='" + password + "'"
			+ ", createdDate='" + createdDate + "'"
			+ ", lastModifiedDate='" + lastModifiedDate + "'"
			+ '}';
	}
	
	private Key getKey(){
		String name = "encrypt.properties";
		Properties props = new Properties();

		InputStream inputStream = ACMEPass.class.getClassLoader().getResourceAsStream(name);
		if (inputStream != null) {
			try {
				props.load(inputStream);
			} catch (IOException e) {
				System.out.println("Error occurred.");
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
					System.out.println("Error occurred.");
				}
			}
		} else {
			System.out.println("Could not find " + name);
		}

		String key = props.getProperty("key");
		byte[] byteArray = hexStringToByteArray(key);
		
		return new SecretKeySpec(byteArray, "AES");
	}
	
	private static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
}
