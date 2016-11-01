<h2>Custom Analytics v1.1</h2>
<br>
<h4>Created by Joao Craveiro</h4>
<br><br>

A web application that is ready to receive information from Aurasma's auras and group it in metrics to then provide a dashboard with results. It's built with Play Framework 2.5, uses EBean for persistence and goes with a PostgreSQL RDB. underneath.
<br>

To list all available auras just navigate to "/".<br>
To see the dashboard of a specific aura navigate to "/auralytics/<auraName>".<br>
<br>

These are the available operations:
<br>
API:
<ul>
<li>New aura: "/newAura/&lt;auraName&gt;" - Creates a new aura with the &lt;auraName&gt;. The name will be used to reference the aura on all other operations and needs to be unique.</li>
<li>New metric: "/newMetric/&lt;auraName&gt;/&lt;metricName&gt;/&lt;plotType&gt;" - Creates a new metric for &lt;auraName&gt; with the name &lt;metricName&gt;. The &lt;metricName&gt; needs to be unique for this aura. The &lt;plotType&gt; currently supports "bar", "circular" or "scoreboard" options and defines what kind of plot should be used to represent this metric in the aura's dashboard.</li>
<li>New metric entry: "/register/&lt;auraName&gt;/&lt;metricName&gt;/&lt;label&gt;/&lt;value&gt;" - Registers a new &lt;value&gt; with a &lt;label&gt; for &lt;metricName&gt; in &lt;auraName&gt;.</li>
<li>New unnamed metric entry: "/register/:aura/:metric/:value" - Registers a new value for &lt;metricName&gt; in &lt;auraName&gt; but first provides a view for the user to assign a label to this entry (to name it).</li>

