<!-- Start of Welcome to my Site -->

	<div id="welcome">

		<div class="content_header">
		<h2><span><!-- Welcome to my Site --></span></h2>
		</div>

		<div class="content">

			<div class="thumbnail_dborder_1"><div class="thumbnail_dborder_2"><div class="thumbnail_dborder_3"><div class="thumbnail_dborder_4">
			<img src="images/welcome_thumbnail.gif" width="118" height="116" alt="" />
			</div></div></div></div>


<p>
<a href="http://en.wikipedia.org/wiki/Pretty_Good_Privacy">Pretty Good Privacy (PGP)</a> is a computer program that provides cryptographic privacy and authentication. 
PGP is often used for signing, encrypting and decrypting e-mails to increase the security of e-mail communications. 
It was created by Philip Zimmermann in 1991.
</p>
<p>
There are several implementations for popular platforms such as Java and .net; however the availability of easy to
implement PGP solutions for ColdFusions is limited. In particular, I found existing implementation rather difficult to use.
Thus, I embarked on this project. To make a long story short, I ended up using the underlying work of the
<a href="bouncycastle.org">league of bouncy castle</a> folks. The complete source code for those libraries can be downloaded from there.
</p>

<p>
You can follow the links above to get started with common PGP tasks such as creating keys/keyrings and 
encrypting/decrypting tasks.
When encrypting or decrypting, you can select to monitor a directory for certain type
of files.
The small wizard will add a directory monitoring task for you in the Coldfusion Administrator.
<br>

<br> - Bilal Soylu

</p>


		</div>

		<div class="clearthis">&nbsp;</div>
	</div>

	<!-- End of Welcome to my Site -->



<!-- Start of Approach -->

	<div id="Approach">

		<div class="content_header">
		<h2><span><!-- Approach --></span></h2>
		</div>

		<div class="content">

			<div class="thumbnail_dborder_1"><div class="thumbnail_dborder_2"><div class="thumbnail_dborder_3"><div class="thumbnail_dborder_4">
			<img src="images/welcome_thumbnail.gif" width="118" height="116" alt="" />
			</div></div></div></div>


		<p>The implementation of this is split up into two parts.</p>
		
		<blockquote>
			<ul>
				<li>A jar library containing Java classes that interact with the bouncy castle libraries.			
				</li>
				<li>A ColdFusion component (PGPController.cfc) to interact with the jar library.</li>
			</ul>
		</blockquote>
		
		<p>For deployment you will need the bouncy castle libraries, the BonCode libraries and the component.
		   Please see the ReadMe file in the SourceInfo.zip file. It describes how to manually deploy the
		   PGP solution.
		</p>


		</div>

		<div class="clearthis">&nbsp;</div>
	</div>

	<!-- End of Welcome to my Site -->


	

	