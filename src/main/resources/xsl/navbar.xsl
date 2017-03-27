<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:i18n="xalan://org.mycore.services.i18n.MCRTranslation"
	xmlns:mabxml="http://www.ddb.de/professionell/mabxml/mabxml-1.xsd"
	exclude-result-prefixes="xsl mabxml">

	<xsl:template match="navbar">
		<nav class="navbar navbar-inverse navbar-fixed-top">
			<div class="container-fluid">

				<div class="navbar-header">
					<button type="button" class="navbar-toggle collapsed"
						data-toggle="collapse" data-target="#navbar" aria-expanded="false"
						aria-controls="navbar">
						<span class="sr-only">Toggle navigation</span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
					</button>
					<a class="navbar-brand" href="{$WebApplicationBaseURL}fachref/start">DuEPublicA</a>
					<xsl:if test="module">
						<a class="navbar-brand" href="{$WebApplicationBaseURL}#">
						<xsl:attribute name="href">
						<xsl:value-of
									select="concat($WebApplicationBaseURL,'fachref/',module/.)" />
						</xsl:attribute>
							<xsl:text>::</xsl:text>
							<xsl:value-of
								select="i18n:translate(concat('nav.fachref.module.',module/.))" />
						</a>
						<xsl:if test="function">
							<a class="navbar-brand" href="#">
								<xsl:text>::</xsl:text>
								<xsl:value-of
									select="i18n:translate(concat('nav.fachref.function.',function/.))" />
							</a>
						</xsl:if>
					</xsl:if>
				</div>
				<xsl:choose>
					<xsl:when test="loggedInAs">
						<div id="navbar" class="navbar-collapse collapse">
							<ul class="nav navbar-nav navbar-right">
								<li>
									<a href="{$WebApplicationBaseURL}protokoll">Knowledge-Base</a>
								</li>
								<li class="dropdown">
									<a href="#" class="dropdown-toggle" data-toggle="dropdown">
										<b>
											<xsl:value-of select="loggedInAs" />
										</b>
										<span class="caret"></span>
									</a>
									<ul class="dropdown-menu" id="login-dp">
										<li>
											<a href="{$WebApplicationBaseURL}logout">Logout</a>
										</li>
										<li>
											<a href="{$WebApplicationBaseURL}fachref/settings/User_Form.xed">Settings</a>
										</li>
										<li>
											<a href="{$WebApplicationBaseURL}fachref/settings/passwordChange">Change passwort</a>
										</li>
										<xsl:if test="admin">
											<li>
												<a href="{$WebApplicationBaseURL}fachref/admin">Admin</a>
											</li>
										</xsl:if>
									</ul>
								</li>
							</ul>
						</div><!--/.navbar-collapse -->
					</xsl:when>
					<xsl:otherwise>
						<div id="navbar" class="navbar-collapse collapse">
							<ul class="nav navbar-nav navbar-right">
								<li>
									<a href="{$WebApplicationBaseURL}index.html">Start</a>
								</li>
								<li class="dropdown">
									<a href="#" class="dropdown-toggle" data-toggle="dropdown">
										<b>Login</b>
										<span class="caret"></span>
									</a>
									<ul id="login-dp" class="dropdown-menu">
										<li>
											<div class="row">
												<div class="col-md-12">
													Login
													<form class="form" role="form" method="post" action="{$WebApplicationBaseURL}userLogging"
														accept-charset="UTF-8" id="login-nav">
														<input type="hidden" name="type" value="login" />
														<div class="form-group">
															<label class="sr-only" for="exampleInputEmail2">Username</label>
															<input type="email" class="form-control" id="exampleInputEmail2"
																placeholder="Username" name="email" required="true"
																autofocus="true" />
														</div>
														<div class="form-group">
															<label class="sr-only" for="exampleInputPassword2">Password</label>
															<input type="password" class="form-control"
																id="exampleInputPassword2" placeholder="Password" name="password"
																required="true" />
														</div>
														<div class="form-group">
															<button type="submit" class="btn btn-primary btn-block">Log In</button>
														</div>
														<div class="checkbox">
															<label>
																<input type="checkbox" name="rememberMe" value="true" />
																Remember Me
															</label>
														</div>
													</form>
												</div>
												<div class="bottom text-center">
													<a href="userRegistration.html">
														<b>New User</b>
													</a>
												</div>
											</div>
										</li>
									</ul>
								</li>
							</ul>
						</div><!--/.navbar-collapse -->

					</xsl:otherwise>
				</xsl:choose>
			</div>
		</nav>
	</xsl:template>
</xsl:stylesheet>