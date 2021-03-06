<#escape x as x?html>
{
    "template_data": {
        "portfolio_name": "${portfolio.name}",
        "id": "${portfolio.uniqueId.objectId}",
    <#if parentNode?has_content>
        "parent_node": "${parentNode.name}",
        "parent_node_id": "${parentNode.uniqueId.objectId}",
    <#else>
    "parent_node": "Root",
	</#if>
            "name": "${node.name}",
            "node": "${node.uniqueId.objectId}"
    },
    "portfolios": [
    	<#list childNodes as item>
			{"name": "${item.name}", "id": "${item.uniqueId.objectId}"}<#if item_has_next>,</#if>
		</#list>
    ],
    "positions": [
    	<#list positions as item>
			{"name": "${item.name}", "quantity": "${item.quantity}", "id": "${item.uniqueId.objectId}"}<#if item_has_next>,</#if>
		</#list>
    ]
}
</#escape>