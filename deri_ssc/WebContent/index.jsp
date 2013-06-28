<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Super Stream Collider</title>
<link href="homepage/css/reset.css" rel="stylesheet" media="screen">
<link href="homepage/css/style.css" rel="stylesheet" media="screen">
<link rel="stylesheet" type="text/css" href="homepage/fancybox/jquery.fancybox-1.3.4.css" media="screen" />

<style>
#browserSupp table,th,td
	{
	border:1px solid black;
	}

td,th
{
height:30px;
width:70px;
vertical-align:middle;text-align:center;
}
</style>

</head>

<body>
<div id="page">
  <aside id="sidebar">
    <nav><a class="logo" href="#top" id="nav-logo"><span style="color:#604343;">Welcome to</span>Super Stream Collider</a>
      <ul>
     	<li class="active" id="nav-1"><a href="#home">Home</a></li>
        <li id="nav-2"><a href="#application">Application</a></li>
        <li id="nav-3"><a href="#documentation">Documentation</a></li> 
        <li id="nav-4"><a href="#work">Demo</a></li>               
        <li id="nav-5"><a href="#contact">Contact</a></li>
      </ul>      
      <div class="bg_bottom"></div>
      <div align="center">
      	<img  style="margin-left:45px;" alt="" src="homepage/images/deri_ie_head.gif">
      </div>
       
    </nav>
  </aside>
  <div id="main-content" >
  	<section id="top"></section><!-- do not remove ;)-->
    <section id="home" style="height:950px;">
      <div id="loader" class="loader"></div>
      <div id="ps_container" class="ps_container"> <span class="ribbon"></span>
        <div class="ps_image_wrapper"> 
          <!-- First initial image --> 
          <img src="homepage/images/1.jpg" alt="" /> </div>
        <!-- Navigation items -->
        <div class="ps_next"></div>
        <div class="ps_prev"></div>
        <!-- Dot list with thumbnail preview -->
        <ul class="ps_nav">
          <li class="selected"><a href="homepage/images/1.jpg" rel="homepage/images/thumbs/1.jpg">Image 1</a></li>
          <li><a href="homepage/images/2.jpg" rel="homepage/images/thumbs/2.jpg">Image 2</a></li>
          <li><a href="homepage/images/3.jpg" rel="homepage/images/thumbs/3.jpg">Image 3</a></li>
          <li><a href="homepage/images/4.jpg" rel="homepage/images/thumbs/4.jpg">Image 4</a></li>     
          <li class="ps_preview">
            <div class="ps_preview_wrapper"> 
              <!-- Thumbnail comes here --> 
            </div>
            <!-- Little triangle --> 
            <span></span> </li>
        </ul>
       
      </div>
      <!--end ps_container-->
      <header class="divider intro-text">
        <h2>Linked Stream Mashups for Everyone </h2>
       <!-- <div class="contact-me"><a class="contact button" href="#contact">Contact Me</a></div>-->
      </header>
      <div class="recent-work columns">
        <h3>Introduction</h3>
	        <p align="justify">Super Stream Collider (SSC) is a platform, which provides a web-based interface 
	        and tools for building sophisticated mashups combining semantically annotated Linked Stream 
	        and Linked Data sources into easy to use resources for applications. The system includes drag&drop 
	        construction tools along with a visual SPARQL/CQELS editor and visualization tools for novice users 
	        while supporting full access and control for expert users at the same time. Tied in with this development 
	        platform is a cloud deployment architecture which enables the user to deploy the generated mashups into a cloud, 
	        thus supporting both the design and deployment of stream-based web applications in a very simple and intuitive way.
	  		</p>
      </div>
      <div class="my-services columns">
        <h3>Features</h3>
        <div class="three-column" style="height:300px;">
          <h4>Drag&Drop editor</h4>
          <p align="justify">The Editor is a programmable Web environment suitable not only for expert users but also for non-expert programmers. 
          The Editor has been designed in a light-weight Web-based workflow for composing mashup data through a drag&drop intuitive interface. 
          A deployment of SSC visual editor is online at <a href="http://superstreamcollider.org/engine.jsp">Super Stream Collider Engine</a>, which provides a user-friendly interface. </p>
        </div>
        <div class="three-column">
          <h4><a href="http://superstreamcollider.org/CqelsEditor/index.html">CQELS/SPARQL visual editor</a></h4>
          <p align="justify">A declarative language has been used for defining stream processing functionalities The data acquisition 
          operator is used to collect or receive data from data sources or gateways and can be pull-based or push-based. 
          By using CQELS the data transformation and alignment can be done to produce a normalized RDF output format, 
          thus a streaming operator streams the outputs of the final operator of a workflow to the consuming stream data applications.</p>
        </div>
        <div class="three-column last">
          <h4>Publish websocket stream</h4>
          <p align="justify">The websocket publisher provides a visual tool for the stream data in the SSC platform. 
          The stream data is bandwidth-efficient via this featured websocket, enabling a streaming interface for 
          publishing the RDF data. It requires a WebSocket client. (some web-browser may not support this feature. e.g. ie)</p>
        </div>
        <p style="color:black;">and more...</p>
      </div>
      
      
    </section> <!--end section-->
    
    <div align="justify">
      	<h4>Browser Support</h4>
      	<p>According the the caniuse.com website, the lastest of five A browsers currently support the latest WebSocket protocol, 
      	while there is one still only offering partial support (see explanation below table). Partial support refers to the websockets 
      	implementation using an older version of the protocol and/or the implementation being disabled by default (due to security issues with the older protocol).
      	<table id = "browsersupp" border="1" summary="WebSocket Support in Modern Browsers">
			<tbody>
			<tr>
			<th>IE</th><th>Firefox</th><th>Chrome</th><th>Safari</th><th>Opera</th><th>iOS Safari</th>
			</tr>
			<tr>
				<td>10~</td>
				<td>14~</td>
				<td>20~</td>
				<td>6~</td>
				<td>11.0 (partial support*)</td>
				<td>6~</td>			
			</tr>
			</tbody>
		</table>		
    </div><br/>
      
    <section id="application" class="clearfix">        	   
    </section> <!--end section-->
    
    <section id="documentation" class="clearfix"> 
       <header>
        <h3>Documentation</h3>
      </header>     
      <p>The SSC tutorial will be available soon</p>
      
    </section> <!--end section-->
    
    <section id="work" class="clearfix">
      <header>
        <h3>Demo</h3>(HD recommended)
      </header><br/>
      <ul class="projects list">
<!--       <p>coming soon...</p> -->
      		<li><iframe width="560" height="315" src="http://www.youtube.com/embed/kVnNaOXATPs" frameborder="0" allowfullscreen></iframe>
			<figcaption align="center" style="color:blue;font-size:15px">Exploring live stream data with SSC</figcaption>
			</li><br/>
			<li><iframe width="560" height="315" src="http://www.youtube.com/embed/XboZ81WvOxU" frameborder="0" allowfullscreen></iframe>
				<figcaption align="center" style="color:blue;font-size:15px">Integrating COSM sensor into SSC</figcaption>
       		 </li>
	      	<li><iframe width="560" height="315" src="http://www.youtube.com/embed/SZDG9GSly9Y" frameborder="0" allowfullscreen></iframe>
				<figcaption align="center" style="color:blue;font-size:15px">CQELS/SPARQL Visual Editor</figcaption>
       		 </li>             
        	<li ><iframe width="560" height="315" src="http://www.youtube.com/embed/PXpNkYpE04Q" frameborder="0" allowfullscreen></iframe>
				<figcaption align="center" style="color:blue;font-size:15px">SPARQL Visualization</figcaption><br/>
		 	</li>	
		 	<li ><iframe width="560" height="315" src="http://www.youtube.com/embed/SYhZTqEiLUA" frameborder="0" allowfullscreen></iframe>
				<figcaption align="center" style="color:blue;font-size:15px"><a href="http://www.siliconrepublic.com/innovation/item/27983-deri-researchers-create-fre">Mobile application using SSC stream platform</a>
				</figcaption><br/>
				<p align="justify">As preliminary research results an example application in the context of smart open-space environments 
		        	(smart cities) on smart mobile devices, a service-oriented application named "Live Data" was developed, to test 
		        	some capabilities defined by the introduced Super Stream Collider (SSC) middleware. 
		        	The mobile sensing application can be seen as part of proof-of-concept for the open data access and control functionalities 
		        	towards enabling the SSC middleware edge support with sensor data being used in across-open spaces and -smart services.</p>
		 	</li>     
	      </ul>
    </section> <!--end section-->
    
        
    <section id="contact" class="clearfix" >
      <header>
        <h3>Get in touch</h3>
      </header><br/>    
      <ul><li><a title="danh.lephuoc@deri.org" href="mailto:danh.lephuoc@deri.org">Danh Le Phuoc</a>(DERI Galway)</li>
      <li><a title="hoan.quoc@deri.org" href="mailto:hoan.quoc@deri.org">Hoan Nguyen Mau Quoc</a>(DERI Galway)
      </li>
      <li><a title="martin.serrano@deri.org" href="mailto:martin.serrano@deri.org">Martin Serrano</a>(DERI Galway)</li>              
      <li><a title="manfred.hauswirth@deri.org" href="mailto:manfred.hauswirth@deri.org">Manfred Hauswirth</a>(DERI Galway)</li>
      </ul><br/>
      <form action="#" method="post">
      	<p><input type="text" name="name"  value="Your Name" id="name" onblur="if (this.value == ''){this.value = 'Your Name'; }" onfocus="if (this.value == 'Your Name') {this.value = '';}" /></p>
        <p><input type="text" name="email" value="Your Email" id="email"  onblur="if (this.value == ''){this.value = 'Your Email'; }" onfocus="if (this.value == 'Your Email') {this.value = '';}" /></p>
        <p><input type="text" name="url" value="Your Site" id="url" onblur="if (this.value == ''){this.value = 'Your Site'; }" onfocus="if (this.value == 'Your Site') {this.value = '';}" /></p>
        <p><textarea cols="20" rows="7" name="message" id="message" onblur="if (this.value == ''){this.value = 'Your Message'; }" onfocus="if (this.value == 'Your Message') {this.value = '';}" >Your Message</textarea></p>
        <p><input id="mailSubmit" type="submit" name="submit" value="Send Message" class="button" /></p>
      </form>
      <div class="social_wrapper">
        	<h3>Where to find us?</h3>
            <ul class="social">
            	<li class="dribble"><a href="#">Dribble</a></li>
                <li class="twitter"><a href="#">Twitter</a></li>
                <li class="lastfm"><a href="#">Last FM</a></li>
                <li class="facebook"><a href="#">Facebook</a></li>
                <li class="location"><a href="#">Location</a></li>
                <li class="forrst"><a href="#">Forrst</a></li>
            </ul>
        </div>
        
      <div class="copyright">
<!--       	<p><small>This <abbr title="Hypertext Markup Language 5">HTML5</abbr> Single Page Portfolio was created <em>by</em> <a href="http://webdesigneraid.com/">WebDesignerAid.com</a> <em>for</em> <a href="http://speckyboy.com/">Speckyboy</a></small></p> -->
      </div><!--end copyright-->
    </section> <!--end section-->
  </div>
</div>
<script type="text/javascript" src="homepage/js/jquery-1.4.3.min.js"></script> 
<script type="text/javascript" src="homepage/js/jquery_003.js"></script> 
<script type="text/javascript" src="homepage/js/jquery.js"></script> 

<!-- The JavaScript --> 
<script type="text/javascript">
			/*
			the images preload plugin
			*/
			(function($) {
				$.fn.preload = function(options) {
					var opts 	= $.extend({}, $.fn.preload.defaults, options),
						o		= $.meta ? $.extend({}, opts, this.data()) : opts;
					return this.each(function() {
						var $e	= $(this),
							t	= $e.attr('rel'),
							i	= $e.attr('href'),
							l	= 0;
						$('<img/>').load(function(i){
							++l;
							if(l==2) o.onComplete();
						}).attr('src',i);	
						$('<img/>').load(function(i){
							++l;
							if(l==2) o.onComplete();
						}).attr('src',t);	
					});
				};
				$.fn.preload.defaults = {
					onComplete	: function(){return false;}
				};
			})(jQuery);
		</script> 
<script type="text/javascript">
			$(function() {
				//some elements..
				var $ps_container		= $('#ps_container'),
					$ps_image_wrapper 	= $ps_container.find('.ps_image_wrapper'),
					$ps_next			= $ps_container.find('.ps_next'),
					$ps_prev			= $ps_container.find('.ps_prev'),
					$ps_nav				= $ps_container.find('.ps_nav'),
					$tooltip			= $ps_container.find('.ps_preview'),
					$ps_preview_wrapper = $tooltip.find('.ps_preview_wrapper'),
					$links				= $ps_nav.children('li').not($tooltip),
					total_images		= $links.length,
					currentHovered		= -1,
					current				= 0,
					$loader				= $('#loader');
				
				/*check if you are using a browser*/	
				var ie 				= false;
				if ($.browser.msie) {
					ie = true;//you are not!Anyway let's give it a try
				}
				if(!ie)
					$tooltip.css({
						opacity	: 0
					}).show();
					
					
				/*first preload images (thumbs and large images)*/
				var loaded	= 0;
				$links.each(function(i){
					var $link 	= $(this);
					$link.find('a').preload({
						onComplete	: function(){
							++loaded;
							if(loaded == total_images){
								//all images preloaded,
								//show ps_container and initialize events
								$loader.hide();
								$ps_container.show();
								//when mouse enters the pages (the dots),
								//show the tooltip,
								//when mouse leaves hide the tooltip,
								//clicking on one will display the respective image	
								$links.bind('mouseenter',showTooltip)
									  .bind('mouseleave',hideTooltip)
									  .bind('click',showImage);
								//navigate through the images
								$ps_next.bind('click',nextImage);
								$ps_prev.bind('click',prevImage);
							}
						}
					});
				});
				
				function showTooltip(){
					var $link			= $(this),
						idx				= $link.index(),
						linkOuterWidth	= $link.outerWidth(),
						//this holds the left value for the next position
						//of the tooltip
						left			= parseFloat(idx * linkOuterWidth) - $tooltip.width()/2 + linkOuterWidth/2,
						//the thumb image source
						$thumb			= $link.find('a').attr('rel'),
						imageLeft;
					
					//if we are not hovering the current one
					if(currentHovered != idx){
						//check if we will animate left->right or right->left
						if(currentHovered != -1){
							if(currentHovered < idx){
								imageLeft	= 75;
							}
							else{
								imageLeft	= -75;
							}
						}
						currentHovered = idx;
						
						//the next thumb image to be shown in the tooltip
						var $newImage = $('<img/>').css('left','0px')
												   .attr('src',$thumb);
						
						//if theres more than 1 image 
						//(if we would move the mouse too fast it would probably happen)
						//then remove the oldest one (:last)
						if($ps_preview_wrapper.children().length > 1)
							$ps_preview_wrapper.children(':last').remove();
						
						//prepend the new image
						$ps_preview_wrapper.prepend($newImage);
						
						var $tooltip_imgs		= $ps_preview_wrapper.children(),
							tooltip_imgs_count	= $tooltip_imgs.length;
							
						//if theres 2 images on the tooltip
						//animate the current one out, and the new one in
						if(tooltip_imgs_count > 1){
							$tooltip_imgs.eq(tooltip_imgs_count-1)
										 .stop()
										 .animate({
											left:-imageLeft+'px'
										  },150,function(){
												//remove the old one
												$(this).remove();
										  });
							$tooltip_imgs.eq(0)
										 .css('left',imageLeft + 'px')
										 .stop()
										 .animate({
											left:'0px'
										  },150);
						}
					}
					//if we are not using a "browser", we just show the tooltip,
					//otherwise we fade it
					//
					if(ie)
						$tooltip.css('left',left + 'px').show();
					else
					$tooltip.stop()
							.animate({
								left		: left + 'px',
								opacity		: 1
							},150);
				}
				
				function hideTooltip(){
					//hide / fade out the tooltip
					if(ie)
						$tooltip.hide();
					else
					$tooltip.stop()
						    .animate({
								opacity		: 0
							},150);
				}
				
				function showImage(e){
					var $link				= $(this),
						idx					= $link.index(),
						$image				= $link.find('a').attr('href'),
						$currentImage 		= $ps_image_wrapper.find('img'),
						currentImageWidth	= $currentImage.width();
					
					//if we click the current one return
					if(current == idx) return false;
					
					//add class selected to the current page / dot
					$links.eq(current).removeClass('selected');
					$link.addClass('selected');
					
					//the new image element
					var $newImage = $('<img/>').css('left',currentImageWidth + 'px')
											   .attr('src',$image);
					
					//if the wrapper has more than one image, remove oldest
					if($ps_image_wrapper.children().length > 1)
						$ps_image_wrapper.children(':last').remove();
					
					//prepend the new image
					$ps_image_wrapper.prepend($newImage);
					
					//the new image width. 
					//This will be the new width of the ps_image_wrapper
					var newImageWidth	= $newImage.width();
				
					//check animation direction
					if(current > idx){
						$newImage.css('left',-newImageWidth + 'px');
						currentImageWidth = -newImageWidth;
					}	
					current = idx;
					//animate the new width of the ps_image_wrapper 
					//(same like new image width)
					$ps_image_wrapper.stop().animate({
					    width	: newImageWidth + 'px'
					},350);
					//animate the new image in
					$newImage.stop().animate({
					    left	: '0px'
					},350);
					//animate the old image out
					$currentImage.stop().animate({
					    left	: -currentImageWidth + 'px'
					},350);
				
					e.preventDefault();
				}				
				
				function nextImage(){
					if(current < total_images-1){
						$links.eq(current+1).trigger('click');
					}else{
						current = 1;
						$links.eq(current-1).trigger('click');
					}
				}
				function prevImage(){
					if(current > 0){
						$links.eq(current-1).trigger('click');
					}else{
						current = 2;
						$links.eq(current+1).trigger('click');
					}
				}
// 				setInterval(function() { 
// 		           	nextImage();
// 		        }, 10000); 
			});
        </script>
        <script type="text/javascript" src="homepage/fancybox/jquery.mousewheel-3.0.4.pack.js"></script>
<script type="text/javascript" src="homepage/fancybox/jquery.fancybox-1.3.4.pack.js"></script>
<script type="text/javascript">

		$(document).ready(function() {
			
			 $("#nav-2").click(function() {			 
				 window.open("http://" + window.location.host + window.location.pathname+'engine.jsp', '_blank');
			});
			 
			$('#mailSubmit').click(function(){ 
				 });
			$('.projects li figure a img').animate({'opacity' : 1}).hover(function() {
				$(this).animate({'opacity' : .5});
			}, function() {
				$(this).animate({'opacity' : 1});
			});
			$('.zoom img').animate({'opacity' : 1}).hover(function() {
				$(this).animate({'opacity' : .5});
			}, function() {
				$(this).animate({'opacity' : 1});
			});

			$("a[rel=work]").fancybox({
				'transitionIn'		: 'elastic',
				'transitionOut'		: 'elastic',
				'titlePosition' 	: 'over',
				'titleFormat'		: function(title, currentArray, currentIndex, currentOpts) {
					return '<span id="fancybox-title-over">Image ' + (currentIndex + 1) + ' / ' + currentArray.length + (title.length ? ' &nbsp; ' + title : '') + '</span>';
				}
			});
			$("a[rel=recent_work]").fancybox({
				'transitionIn'		: 'elastic',
				'transitionOut'		: 'elastic',
				'titlePosition' 	: 'over',
				'titleFormat'		: function(title, currentArray, currentIndex, currentOpts) {
					return '<span id="fancybox-title-over">Image ' + (currentIndex + 1) + ' / ' + currentArray.length + (title.length ? ' &nbsp; ' + title : '') + '</span>';
				}
			});
			
			
			
		});
	</script>
</body>
</html>
