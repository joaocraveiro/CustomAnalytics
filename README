<h2>Custom Analytics v1.0</h2>
<br>
<h4>Created by Joao Craveiro. September 2015</h4>
<br><br>

A web application that is ready to receive information from Aurasma's auras and group it in metrics to then provide a dashboard with results. It's built with Play Framework 2.4.3, uses EBean for persistence and is hosted on Heroku with a PostgreSQL RDB underneath.
<br>
To list all available auras just navigate to "/".<br>
To see the dashboard of a specific aura navigate to "/auralytics/<auraName>".<br>
<br>

These are the available operations:
<br>
API:
<ul>New aura: "/newAura/<auraName>" - Creates a new aura with the <auraName>. The name will be used to reference the aura on all other operations and needs to be unique.
<li>New metric: "/newMetric/<auraName>/<metricName>/<plotType>" - Creates a new metric for <auraName> with the name <metricName>. The &lt;metricName> needs to be unique for this aura. The <plotType> currently supports "bar", "circular" or "scoreboard" options and defines what kind of plot should be used to represent this metric in the aura's dashboard.</li>
<li>New metric entry: "/register/&lt;auraName&gt;/&lt;metricName&gt;/&lt;label&gt;/&lt;value&gt;" - Registers a new &lt;value&gt; with a &lt;label&gt; for &lt;metricName&gt; in &lt;auraName&gt;.</li>
<li>New unnamed metric entry: "/register/:aura/:metric/:value" - Registers a new value for <metricName> in <auraName> but first provides a view for the user to assign a label to this entry (to name it).</li>

