package unidue.ub.userauth;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SaltedAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.PrincipalCollection;

public class BibliometricsRealm extends JdbcRealm {
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		// identify account to log to
		UsernamePasswordToken userPassToken = (UsernamePasswordToken) token;
		final String username = userPassToken.getUsername();
		if (username == null) {
			return null;
		}
		// read password hash and salt from db
		final User user = UserDAO.getUser(username);
		if (user == null) {
			return null;
		}
		// return salted credentials
		SaltedAuthenticationInfo info = new SaltedAuthInfo(username, user.getPassword(), user.getSalt());
		return info;
	}
	
	@Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) throws AuthenticationException {
	    String email = (String)principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setRoles(UserRoleDAO.getRoles(email));
        authorizationInfo.setStringPermissions(RolePermissionDAO.getPermission(email));
        return authorizationInfo;
    }
	
	protected String dataSourceName;
	public String getDataSourceName() {
		return dataSourceName;
	}
}
