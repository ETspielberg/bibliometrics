<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:i18n="xalan://org.mycore.services.i18n.MCRTranslation"
	xmlns:mabxml="http://www.ddb.de/professionell/mabxml/mabxml-1.xsd"
	exclude-result-prefixes="xsl xalan i18n mabxml">

	<xsl:output encoding="UTF-8" method="html" media-type="text/html"
		doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
		doctype-system="http://www.w3.org/TR/html401/loose.dtd" indent="yes"
		xalan:indent-amount="2" />

	<!-- ============ Parameter von MyCoRe LayoutService ============ -->

	<xsl:param name="WebApplicationBaseURL" />
	<xsl:param name="ServletsBaseURL" />
	<xsl:param name="RequestURL" />
	<xsl:param name="CurrentLang" />
	<xsl:param name="DefaultLang" />

	<!-- ======== HTML Seitenlayout ======== -->

	<xsl:template match="/">
		<html>
			<head>
				<meta charset="utf-8" />
				<meta http-equiv="X-UA-Compatible" content="IE=edge" />
				<meta name="viewport" content="width=device-width, initial-scale=1" />
				<!-- The above 3 meta tags *must* come first in the head; any other head 
					content must come *after* these tags -->

				<title>DuEPublicA :: Login</title>

				<script src="{$WebApplicationBaseURL}js/ie-emulation-modes-warning.js"></script>
				<script src="{$WebApplicationBaseURL}js/ie10-viewport-bug-workaround.js"></script>

				<link href="{$WebApplicationBaseURL}css/bootstrap.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/ie10-viewport-bug-workaround.css"
					rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/jumbotron.css" rel="stylesheet" />

				<link rel="icon" href="{$WebApplicationBaseURL}img/favicon.ico" />
			</head>
			<body>
				<div class="container">
					<div class="jumbotron">

						<h1>DuEPublicA</h1>
						<p>Informationen rund um Ihre Publikationen.</p>
						<div class="col-md-6 col-md-offset-3">
							<h3>Login</h3>
							<xsl:if test="userLogging/message">
								<xsl:apply-templates select="userLogging/message">
								</xsl:apply-templates>
							</xsl:if>
							<div class="panel panel-default">
								<div id="login-panel" class="panel-body">
									<form name="loginform" action="userLogging" method="POST"
										accept-charset="UTF-8" role="form">
										<fieldset>
											<div class="form-group">
												<input class="form-control" placeholder="Kennung"
													name="username" type="text" autofocus="true"></input>
											</div>
											<div class="form-group">
												<input class="form-control" placeholder="Passwort"
													name="password" type="password" value=""></input>
											</div>
											<div class="checkbox">
												<label>
													<input name="rememberMe" type="checkbox" value="true">
														Remember Me</input>
												</label>
											</div>
											<input class="btn btn-lg btn-success btn-block" type="submit"
												value="Login"></input>
										</fieldset>
									</form>
									</div>
							</div>

						</div>
					</div>
				</div>


				

				<script
					src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
				<script src="js/bootstrap.min.js"></script>

				<script src="js/ie10-viewport-bug-workaround.js"></script>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="message">
		<div class="alert alert-danger">
			<xsl:value-of select="i18n:translate(.)" />
		</div>
	</xsl:template>

</xsl:stylesheet>
