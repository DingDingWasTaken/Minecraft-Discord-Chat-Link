# Minecraft Discord Chat Link
<p>Fabric Minecraft (1.20.1) mod with integrated discord bot for flexible linkage between servers.</p>
<p>(Pixel art icon originally by u/IntermediateGameDev I'm not cool enough for that.)</p>
<p>&nbsp;</p>
<h3>How to Set Up:</h3>
<p>&nbsp;</p>
<h4>Set up the Discord Bot:</h4>
<ol>
<li>Go to&nbsp;<a href="https://discord.com/developers/">https://discord.com/developers/</a>&nbsp;and click "<strong>New Application</strong>" in the top right corner</li>
<li>Name the application anything you desire, the mod will default the bot with preset aesthetics once registered</li>
<li>Go to <strong>&gt;Bot</strong>, click `<strong>Reset Token</strong>` and copy and paste that somewhere secure (you will need to keep this on hand for later use &amp; when server restarts)</li>
</ol>
<h4>Add the Bot to your server:</h4>
<ol>
<li>Go to <strong>&gt;OAuth2&gt;URL Generator</strong>, select the box that says `<strong>bot</strong>`, then select `<strong>Read Messages/View Channels</strong>` and `<strong>Send Messages</strong>`</li>
<li>At the bottom of the page, copy the generated URL and paste it into the server you want the Minecraft chat to be linked to and click the link</li>
<li>The bot will only go online when the Minecraft server is running and the bot is initialized</li>
</ol>
<h4>Set up the Minecraft Server:</h4>
<ol>
<li>Download the <a href="https://github.com/DingDingWasTaken/Minecraft-Discord-Chat-Link/blob/main/DiscordChatLink-1.0.jar">Fabric Mod</a>&nbsp;jar file and run it on a Fabric Minecraft 1.20.1 server</li>
<li>As an administrator in the server, run `<strong>/discord register &lt;</strong>bot&nbsp;token<strong>&gt;</strong>` (The bot token is created in the bot set up). With successful registration, the bot in your discord should now go online.</li>
<li>In the discord with the bot, type `<strong>~help</strong>` to get started</li>
</ol>
<p>NOTICE: every time the server is stopped and started or the bot is terminated, the bot will have to be re-initialized. This is done by redoing the&nbsp;`<strong>/discord register &lt;</strong>bot&nbsp;token<strong>&gt;</strong>` command in the server</p>
