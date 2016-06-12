<%@ page contentType="text/html; charset=utf-8" language="java" pageEncoding="utf-8" isELIgnored="false" errorPage="" %>

<html>

<head>
<title>Locations</title>
<script type="text/javascript">
function findOrModify(action) {
	action = '/tops-farerule-mongo-front/location/' + action;
	find_and_modify.action = action;
	find_and_modify.submit();
}
function removeConfirm() {
	if (!confirm("确认删除？")) { 
		window.event.returnValue = false; 
	}
}
</script>
</head>

<body>
<img src="/tops-farerule-mongo-front/resources/images/logo.png" alt="logo"/>

<h4>Location数据库&emsp;&emsp;&emsp;&emsp;
<!-- RETURN -->
<a href="/tops-farerule-mongo-front/index.html">Return</a>
</h4>

<!-- COUNT -->
<form id="count" method="post" action="/tops-farerule-mongo-front/location/count">
<input type="submit" value="count">&nbsp;${count_result}
</form>
<!-- FIND AND MODIFY -->
<form id="find_and_modify" method="post">
EnLoc:&nbsp;<input type="text" name="enLoc" size="30" value="${find_result_en}"><br/>
CnLoc:&nbsp;<input type="text" name="cnLoc" size="30" value="${find_result_cn}"><br/>
<input type="button" name="find" value="find" onclick="findOrModify('find')">&nbsp;${find_result}<br/>
<input type="button" name="modify" value="modify" onclick="findOrModify('insert')">&nbsp;${insert_result}
</form>
<!-- REMOVE -->
<form id="remove" method="post" action="/tops-farerule-mongo-front/location/remove">
EnLoc:&nbsp;<input type="text" name="enLoc" size="30"/><br/>
<input type="submit" value="remove" onclick="removeConfirm()">&nbsp;${remove_result}
</form>
<!-- REMOVE ALL -->
<form id="remove_all" method="post" action="/tops-farerule-mongo-front/location/removeAll">
<input type="submit" value="remove all" onclick="removeConfirm()">&nbsp;${remove_all_result}
</form>
<!-- FIND NEW -->
<form id="find_new" method="post" action="/tops-farerule-mongo-front/location/findNew">
<input type="submit" value="find new">&nbsp;${find_new_result}
</form>
<!-- FIND ALL -->
<form id="find_all" method="post" action="/tops-farerule-mongo-front/location/findAll">
<input type="submit" value="find all">&nbsp;${find_all_result}
</form>

</body>

</html>