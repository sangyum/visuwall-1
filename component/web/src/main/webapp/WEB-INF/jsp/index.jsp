<%@ page language="java" contentType="text/html; charset=utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Visuwall</title>
	<link rel="shortcut icon" href="favicon.ico" />
	
	
<link rel="stylesheet" href="res/css/project.css" type="text/css">
<link rel="stylesheet" href="res/css/loader.css" type="text/css">
<link rel="stylesheet" href="res/css/wallForm.css" type="text/css">
<link rel="stylesheet" href="res/css/default.css" type="text/css">
<link rel="stylesheet" href="res/css/navigation.css" type="text/css">
<link rel="stylesheet" href="res/css/jquery.multiselect.css" type="text/css">
<link rel="stylesheet" href="res/css/jquery.marquee.min.css" type="text/css">
<link rel="stylesheet" href="res/css/jquery.selectBox.css" type="text/css">
<link rel="stylesheet" href="res/css/jquery/jquery-ui-1.8.11.custom.css" type="text/css">

	<script type="text/javascript">curl = {baseUrl: 'res/js'};</script>
	<script type="text/javascript" src="res/js/curl.js"></script>
	
	<script type="text/javascript">
	curl(['main'], function(main) {
			main.start(${jsData});
	});
	</script>
</head>
<body class="visuwallBack">
<div id="navigationContainer">
	<div id="modal" style="display: none"></div>
	<ul id="navigation">
		<li id="name">Visuwall</li>
		<li id="wallSelector">
			<select id="wallSelect" name="wall"></select>
			<button id="edit" type="button" value="Edit">Edit</button>
			<button id="add" type="button" value="Add">Add</button>
		</li>
		<li><div id="fontSizeSlider"></div></li>
		<li id="helper"><img id="helperimg" src="res/img/helpS.png"></li>
		<li id="about"><a href="http://visuwall.awired.net/"
			title="Visuwall">${version}</a></li>
	</ul>
</div>

<ul id="projectsTable"></ul>
<div id="overlay"></div>
<div id="contents">
	<div style="display:none" id="formCreation">

		<form id="wallForm" action="/visuwall-web/wall/create.html" method="post"> 
		<input id="id" name="id" type="hidden" value=""/> 

		<label for="name">Name</label> 
		<input id="name" name="name" name="name" class="ui-widget-content ui-corner-all" type="text" type="text" value=""/> 

		<div id="softTabs"> 
			<ul> 
				<li><a href="#tabs-0">New</a> <span class="ui-icon ui-icon-close">Remove
						Tab</span></li> 
				<li><div id="softAdd" class="ui-state-default ui-corner-all"
						title=".ui-icon-plusthick"> 
						<span class="ui-icon ui-icon-plusthick"></span> 
					</div> 
				</li> 
			</ul> 
			<div id="tabs-0"> 
				<input id="softwareAccesses0.id" name="softwareAccesses[0].id" type="hidden" value=""/> 
				<label for="softwareAccesses0.url">Url</label> 
				<label id="softwareAccesses0.urlcheck" class="check"></label>
				<input id="softwareAccesses0.url" name="softwareAccesses[0].url" class="ui-widget-content ui-corner-all url" type="text" value=""/>

				<fieldset class="propertiesField">
					<legend>Properties</legend>
					<div class="test42" style="float:left">	
	 					<label for="softwareAccesses0.login">Login</label> 
						<input id="softwareAccesses0.login" name="softwareAccesses[0].login" class="ui-widget-content ui-corner-all" type="text" value=""/> 
					</div>
					<div class="test42" style="float:left">	
						<label for="softwareAccesses0.password">Password</label> 
						<input id="softwareAccesses0.password" name="softwareAccesses[0].password" class="ui-widget-content ui-corner-all" type="password" value=""/> 
					</div>
				</fieldset>

				<fieldset class="timerField">
					<legend>Timers</legend>

					<div class="test42" style="float:left">	
						<label for="softwareAccesses0.projectFinderDelaySecond">Software Refresh Time : <span class="bold projectFinderDelaySecond">45s</span></label>
						<div class="slider projectFinderDelaySecondSlider"></div>
						<input type="hidden" id="softwareAccesses0.projectFinderDelaySecond" name="softwareAccesses[0].projectFinderDelaySecond" class="ui-widget-content ui-corner-all" value=""/>
					</div>

					<div class="test42" style="float:left">	
						<label for="softwareAccesses0.projectStatusDelaySecond">Project Refresh Time <span class="bold projectStatusDelaySecond">45s</span></label>
						<div class="slider projectStatusDelaySecondSlider"></div>
						<input type="hidden" id="softwareAccesses0.projectStatusDelaySecond" name="softwareAccesses[0].projectStatusDelaySecond" class="ui-widget-content ui-corner-all" value=""/>
					</div>
				</fieldset>

				<div style="clear: both;"></div>
				<fieldset class="buildField" style="display: none">
					<legend>Builds</legend>

					<label for="softwareAccesses0.allProject">All Projects</label> 
					<input type="checkbox" id="softwareAccesses0.allProject" name="softwareAccesses[0].allProject" value="true" /> 
					<input type="hidden" id="_softwareAccesses0.allProject" name="_softwareAccesses[0].allProject" value="true" />

					<div class="projectNamesSection" style="float:left">	
						<label for="softwareAccesses0.projectNames">Projects</label>
						<select id="softwareAccesses0.projectNames" name="softwareAccesses[0].projectNames" multiple="multiple" size="5">
						</select>
					</div>
					<div class="projectNamesSection" style="float:right">
						<label for="softwareAccesses0.viewNames">Views</label>
						<select id="softwareAccesses0.viewNames" name="softwareAccesses[0].viewNames" multiple="multiple" size="5">
						</select>
					</div>
				</fieldset>

				<div style="clear: both;"></div>
			</div>
		</div>
			<input class="submit" type="submit" value="Save"/> 
			<div class="loader"></div>
			<div class="success"></div>
			<div class="failure"></div>
<!-- 			<input id="delete" type="button" value="Delete"/>  -->
		</form>	
	</div>
	<div style="display:none" id="helperdiv">
		<table>
			<thead>
				<tr>
					<th>Color</th>
					<th>Project State</th>
				</tr>
			</thead>

			<tr>
				<td class="success-state"></td>
				<td>Project in Success</td>
			</tr>
			<tr>
				<td class="failure-state"></td>
				<td>Project compilation failure</td>
			</tr>
			<tr>
				<td class="new-state"></td>
				<td>New Project without any builds</td>
			</tr>
			<tr>
				<td class="aborted-state"></td>
				<td>Last build was aborted</td>
			</tr>
			<tr>
				<td class="unstable-state"></td>
				<td>project with fail test(s)</td>
			</tr>
			<tr>
				<td class="notbuilt-state"></td>
				<td>the build was not able to run</td>
			</tr>
			<tr>
				<td class="unknown-state"></td>
				<td>Unknown state of project</td>
			</tr>
		</table>
	</div>
</div>

</body>
</html>