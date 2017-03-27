<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:i18n="xalan://org.mycore.services.i18n.MCRTranslation"
	xmlns:mabxml="http://www.ddb.de/professionell/mabxml/mabxml-1.xsd"
	exclude-result-prefixes="xsl xalan i18n mabxml">
	
	<xsl:include href="navbar.xsl" />

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

	<xsl:template match="/userAdmin">
		<html>
			<head>
				<meta charset="utf-8" />
				<meta http-equiv="X-UA-Compatible" content="IE=edge" />
				<meta name="viewport" content="width=device-width, initial-scale=1" />
				<!-- The above 3 meta tags *must* come first in the head; any other head 
					content must come *after* these tags -->

				<title>FachRef-Assistent :: Nutzer-Administration</title>

				<script src="{$WebApplicationBaseURL}js/ie-emulation-modes-warning.js"></script>
				<script src="{$WebApplicationBaseURL}js/ie10-viewport-bug-workaround.js"></script>

				<link href="{$WebApplicationBaseURL}css/bootstrap.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/ie10-viewport-bug-workaround.css"
					rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/jumbotron.css" rel="stylesheet" />

				<link rel="icon" href="{$WebApplicationBaseURL}img/favicon.ico" />
			</head>
			<body>
				<xsl:apply-templates select="navbar" />
						<xsl:choose>
							<xsl:when test="user">
							<div class="jumbotron">
								<div class="container" >
									<h1>Nutzerübersicht</h1>
									<p>Einstellungen für jeden Nutzer</p>
								</div>
							</div>
							<div class="container">
								<table id="sortableTable" class="table table-hover">
									<thead>
										<tr>
											<th>E-Mail </th>
											<th></th>
											<th>Rolle(n) </th>
										</tr>
									</thead>
									<tbody>
										<xsl:apply-templates select="user" />
									</tbody>
								</table>
							</div>
							</xsl:when>
							<xsl:otherwise>
							<div class="container">
								<div class="col-md-10 col-md-offset-1 main">
									<p>Es wurden keine Nutzer gefunden.</p>
								</div>
							</div>
							</xsl:otherwise>
						</xsl:choose>
				<script src="js/bootstrap.min.js"></script>
				<script src="js/ie10-viewport-bug-workaround.js"></script>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="user">
		<xsl:variable name="email">
			<xsl:value-of select="email/." />
		</xsl:variable>
		<tr>
		<td><xsl:value-of select="email/." />
		</td>
		<td>
		<form class="form" role="form" method="post" action="userAdmin" >
			<input type="hidden" name="name" value="{$email}" />
			<input type="hidden" name="type" value="userDelete" />
			<input type="submit" class="btn btn-xs btn-danger" value="Nutzer löschen" />
		</form>
		<form class="form" role="form" method="post" action="userAdmin" >
			<input type="hidden" name="email" value="{$email}" />
			<input type="text" name="newPassword" placeholder="neues Passwort" />
			<input type="submit" class="btn btn-xs btn-danger" value="Passwort setzen" />
		</form>
		</td>
		<td>
		<table class="table table-striped">
		<xsl:if test="role">
			<xsl:apply-templates select="role"/>
		</xsl:if>
		<tr>
		<form class="form" role="form" method="post" action="userAdmin" >
		<td>
			<input type="hidden" name="name" value="{$email}" />
			<input type="hidden" name="type" value="roleAdd" />
			<select name="roleName" class="form-control  input-sm">
                  <option value="fachreferent">Fachreferent</option>
                  <option value="userAdmin">Admin</option>
                  <option value="medienbearbeitung"> Medienbearbeitung </option>
                  <option value="guest">Gast</option>
            </select>
            </td>
            <td>
			<input type="submit" class="btn btn-xs btn-success" value="hinzufügen" />
			</td>
		</form>
		</tr>
		</table>
		</td>
		</tr>
	</xsl:template>

	<xsl:template match="role">
	<xsl:variable name="role">
			<xsl:value-of select="." />
		</xsl:variable>
		<xsl:variable name="email">
			<xsl:value-of select="../email/." />
		</xsl:variable>
		
	<tr>
	<td>
	<xsl:value-of select="." />
	</td>
	<td>
	<form id="form-{$email}-{$role}" class="form" role="form" method="post" action="userAdmin" >
			<input type="hidden" name="name" value="{$email}" />
			<input type="hidden" name="type" value="roleDelete" />
			<input type="hidden" name="roleName" value="{$role}" />
			<input type="submit" class="btn btn-xs btn-danger" value="löschen" />
		</form>
		</td>
	</tr>
	</xsl:template>
</xsl:stylesheet>
