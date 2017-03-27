package unidue.ub.userauth;

import org.apache.shiro.authc.SaltedAuthenticationInfo;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;

public class SaltedAuthInfo implements SaltedAuthenticationInfo {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private final String email;
	private final String password;
	private final String salt;

	public SaltedAuthInfo(String email, String password, String salt) {
		this.email = email;
		this.password = password;
		this.salt = salt;
	}

	@Override
	public PrincipalCollection getPrincipals() {
		PrincipalCollection coll = new SimplePrincipalCollection(email, email);
		return coll;
	}

	@Override
	public Object getCredentials() {
		return password;
	}

	@Override
	public ByteSource getCredentialsSalt() {
		return new SimpleByteSource(Base64.decode(salt));
	}

}
