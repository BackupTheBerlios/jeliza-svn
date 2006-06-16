<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>

                <style type="text/css">
                    * html .noIe { display: none; }
                </style>

<title>Tobias Website | Eliza</title>
<link rel="stylesheet" type="text/css" href="/styles.css" />
<meta name="keywords" content="" />
<meta name="description" content="" />
<link rel="stylesheet" type="text/css" href="/files/main.css" media="screen" />

</head>
<body style="overflow-x: hidden !important; overflow-y: auto;">

    <div><a name="top"></a></div>
    <div id="website">
        <div id="bars">
            <div id="content">
                <br />
                <br />
                <br />
                <br />
                <br />

                <br />
                <div style="padding: 0; width: 135px; margin: 0 0 0 0; z-index: 13; position: absolute; top: 110px; right: 0; " class="nav"> 
                    <br />
                    <ul style="list-style-type: none; margin:0px; padding:0px;"><li><a href="index.php?article_id=1">Home</a></li><li><a href="index.php?article_id=16">Software</a><ul style="list-style-type: none; margin-left:10px; padding-left:10px;"><li><a href="index.php?article_id=17">Programme</a></li><li><a href="index.php?article_id=18">Bibliotheken</a></li><li><a href="index.php?article_id=29">Dokumentation</a></li></ul></li><li><a href="index.php?article_id=31">Linux</a><ul style="list-style-type: none; margin-left:10px; padding-left:10px;"><li><a href="index.php?article_id=32">Ubuntu-Mirror</a></li><li><a href="index.php?article_id=33">Der Editor vi</a></li></ul></li><li><a href="index.php?article_id=40">Gästebuch</a></li><li><a href="index.php?article_id=34">Der Server</a></li><li class="active"><a class="current" href="index.php?article_id=36">Gemischtes</a><ul style="list-style-type: none; margin-left:10px; padding-left:10px;"><li class="active"><a class="current" href="index.php?article_id=37">JEliza</a></li></ul></li><li><a href="index.php?article_id=8">Sitemap</a></li><li><a href="index.php?article_id=30">Impressum</a></li></ul>                    <br class="noIe" />

                </div>
                <div style="width: 550px !important; padding-right: 10px; border-right: 1px solid gray; 
                margin-top: 5px;">
                    <a name="oben"></a>

                    <br />
                    
                    
                    

<jsp:setProperty name="cart" property="*" />
<%
	cart.processRequest(request);
%>

<br> You have the following items in your cart:
<ol>
<% 
	String[] items = cart.getItems();
	for (int i=0; i<items.length; i++) {
%>
<li> <% out.print(util.HTMLFilter.filter(items[i])); %> 
<%
	}
%>
</ol>

<form type=POST action=carts.jsp>
<BR>
Please enter item to add or remove:
<br>
Add Item:

<SELECT NAME="item">
<OPTION>Beavis & Butt-head Video collection
<OPTION>X-files movie
<OPTION>Twin peaks tapes
<OPTION>NIN CD
<OPTION>JSP Book
<OPTION>Concert tickets
<OPTION>Love life
<OPTION>Switch blade
<OPTION>Rex, Rugs & Rock n' Roll
</SELECT>


<br> <br>
<INPUT TYPE=submit name="submit" value="add">
<INPUT TYPE=submit name="submit" value="remove">

</form>


                     <br />
                </div>
            </div>
            <div id="footer">
                <div id="drop">&#160;</div>
            </div>
        </div>
    </div>

</body>
</html>