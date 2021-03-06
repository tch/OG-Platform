<!doctype html>
<!--[if IE 8 ]><html lang="en" class="no-js ie8"><![endif]-->
<!--[if IE 9 ]><html lang="en" class="no-js ie9"><![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"> <!--<![endif]-->
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="SKYPE_TOOLBAR" content="SKYPE_TOOLBAR_PARSER_COMPATIBLE" />
<title>OpenGamma</title>
<!--[if lt IE 9]><script type="text/javascript" src="/prototype/scripts/lib/html5.js"></script><![endif]-->
${ogStyle.print('og_all.css', 'all')}
</head>
<body>
<div id="container" class="OG-clearFix">
  <#include "modules/common/og.common.masthead.ftl">
  <#include "modules/common/og.common.search_results.ftl">
  <#include "modules/common/og.common.details.ftl">
  <#include "modules/common/og.common.analytics.ftl">
</div>
<!--[if IE]>${ogScript.print('ie.js')}<![endif]-->
${ogScript.print('og_all.js')}
</body>
</html>