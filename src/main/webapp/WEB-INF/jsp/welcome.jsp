<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>

<!-- Access the bootstrap Css like this,
		Spring boot will handle the resource mapping automcatically -->
<link rel="stylesheet" type="text/css"
	href="webjars/bootstrap/3.3.7/css/bootstrap.min.css" />

<!--
	<spring:url value="/css/main.css" var="springCss" />
	<link href="${springCss}" rel="stylesheet" />
	 -->
<c:url value="/css/main.css" var="jstlCss" />
<link href="${jstlCss}" rel="stylesheet" />

</head>
<title>Web-Crawler</title>
<body>

	<nav class="navbar navbar-inverse">
		<div class="container">
			<div class="navbar-header">
				<a class="navbar-brand" href="#">Web Crawler</a>
			</div>
			<div id="navbar" class="collapse navbar-collapse">
				<ul class="nav navbar-nav">
					<li class="active"><a href="#">Home</a></li>
					<li><a href="#about">About</a></li>
				</ul>
			</div>
		</div>
	</nav>

	<div class="container">

		<div class="starter-template">
			<%-- <c:if test="${!empty isError}">
				<h2>Please Input the Valid url/link</h2>
			</c:if> --%>
			<form action="/generateSiteMap" method="post"
				onsubmit="return validateForm();">
				<table class="alerts" cellspacing="0">
					<tr>
						<td class="alertHd" colspan="2">Please Input the url/link which you want
							to crawl</td>
					</tr>
					<tr>
						<td class="alertHd">Crawl Url</td>
						<td class="alertBod"><input type="text" name="crawlurl"
							id="crawlurl"></td>
					</tr>
					<tr>
						<td class="alertHd">Max No of Pages</td>
						<td class="alertBod"><input type="text" name="maxPages"
							id="maxPages"></td>
					</tr>
					<tr>
						<td colspan="2">
							<button type="submit">Generate SiteMap</button>
						</td>
					</tr>
				</table>
			</form>
		</div>

	</div>

	<script type="text/javascript">
		function validateForm() {
			var crawlUrl = document.getElementById("crawlurl").value;
			var maxPages = document.getElementById("maxPages").value;
			var regex = /(http|https):\/\/(\w+:{0,1}\w*)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%!\-\/]))?/;
			document.getElementById("crawlurl").style.border="thin solid grey";
			document.getElementById("maxPages").style.border="thin solid grey";
			if (crawlUrl == "" || !regex .test(crawlUrl) ) {
				alert("Please input the Valid Crawl Url");
				document.getElementById("crawlurl").style.border="thin solid red";
				return false;
			} else if (maxPages == "") {
				alert("Please input the Max No of Pages");
				document.getElementById("maxPages").style.border="thin solid red";
				return false;
			}
			return true;
		}
	</script>
</body>

</html>