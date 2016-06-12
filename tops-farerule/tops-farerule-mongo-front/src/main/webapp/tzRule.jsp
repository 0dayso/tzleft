<%@ page contentType="text/html; charset=utf-8" language="java" pageEncoding="utf-8" isELIgnored="false" errorPage="" %>

<html>

<head>
<title>Tz Rules</title>
<script type="text/javascript">
function findOrModify(action) {
	action = '/tops-farerule-mongo-front/tzRule/' + action;
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

<h4>Jpecker解析结果数据库&emsp;&emsp;&emsp;&emsp;
<!-- RETURN -->
<a href="/tops-farerule-mongo-front/index.html">Return</a>
</h4>

<!-- COUNT -->
<form id="count" method="post" action="/tops-farerule-mongo-front/tzRule/count">
<input type="submit" value="count">&nbsp;${count_result}
</form>
<!-- FIND AND MODIFY -->
<form id="find_and_modify" method="post">
Source:&nbsp;jpecker<input type="radio" name="source" value="jpecker" checked />
&nbsp;paperfare<input type="radio" name="source" value="paperfare" /><br/>
ID:&nbsp;<input type="text" name="id" size="40" value="${find_result_id}" /><br/>
MinStay:<br/><textarea rows="3" cols="50" name="content_text_6">${find_result_content_6}</textarea><br/>
MaxStay:<br/><textarea rows="3" cols="50" name="content_text_7">${find_result_content_7}</textarea><br/>
TravelDate:<br/><textarea rows="3" cols="50" name="content_text_14">${find_result_content_14}</textarea><br/>
Penalties:<br/><textarea rows="10" cols="50" name="content_text_16">${find_result_content_16}</textarea><br/>
<input type="button" name="find" value="find" onclick="findOrModify('find')">&nbsp;${find_result}<br/>
<input type="button" name="modify" value="modify" onclick="findOrModify('insert')">&nbsp;${insert_result}
</form>
<!-- REMOVE -->
<form id="remove" method="post" action="/tops-farerule-mongo-front/tzRule/remove">
Source:&nbsp;jpecker<input type="radio" name="source" value="jpecker" checked />
&nbsp;paperfare<input type="radio" name="source" value="paperfare" /><br/>
ID:&nbsp;<input type="text" name="id" size="40"/><br/>
<input type="submit" value="remove" onclick="removeConfirm()">&nbsp;${remove_result}
</form>
<!-- REMOVE ALL -->
<form id="remove_all" method="post" action="/tops-farerule-mongo-front/tzRule/removeAll">
<input type="submit" value="remove all" onclick="removeConfirm()">&nbsp;${remove_all_result}<br/>
</form>
<!-- FIND ALL -->
<form id="find_all" method="post" action="/tops-farerule-mongo-front/tzRule/findAll">
<input type="submit" value="find all">&nbsp;${find_all_result}
</form>

</body>

</html>