<%@ page contentType="text/html; charset=utf-8" language="java" pageEncoding="utf-8" isELIgnored="false" errorPage="" %>

<html>

<head>
<title>RuleInfo</title>
<script type="text/javascript">
function removeConfirm() {
	if (!confirm("确认删除？")) { 
		window.event.returnValue = false; 
	}
}
</script>
</head>

<body>
<img src="/tops-farerule-mongo-front/resources/images/logo.png" alt="logo"/>

<h4>RuleInfo数据库&emsp;&emsp;&emsp;&emsp;
<!-- RETURN -->
<a href="/tops-farerule-mongo-front/index.html">Return</a>
</h4>

<!-- COUNT -->
<form id="count" method="post" action="/tops-farerule-mongo-front/ruleInfo/count">
<input type="submit" value="count">&nbsp;${count_result}
</form>
<!-- FIND -->
<form id="find" method="post" action="/tops-farerule-mongo-front/ruleInfo/find">
RuleInfo ID:&nbsp;<input type="text" name="id" size="40" value="${find_result_id}"/><br/>
Content:<br/><textarea rows="20" cols="60" name="text" readonly="readonly">${find_result_text}</textarea><br/>
<input type="submit" value="find">&nbsp;${find_result}
</form>
<!-- REMOVE -->
<form id="remove" method="post" action="/tops-farerule-mongo-front/ruleInfo/remove">
RuleInfo ID:&nbsp;<input type="text" name="id" size="40"/><br/>
<input type="submit" value="remove" onclick="removeConfirm()">&nbsp;${remove_result}
</form>
<!-- REMOVE ALL -->
<form id="remove_all" method="post" action="/tops-farerule-mongo-front/ruleInfo/removeAll">
<input type="submit" value="remove all" onclick="removeConfirm()">&nbsp;${remove_all_result}
</form>
<!-- FIND ALL -->
<form id="find_all" method="post" action="/tops-farerule-mongo-front/ruleInfo/findAll">
<input type="submit" value="find all">&nbsp;${find_all_result}
</form>

</body>

</html>