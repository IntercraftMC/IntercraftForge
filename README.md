# Intercraft Forge

Our custom Forge version adds all sorts of improvements to the standard Forge version, from performance improvements to additional bug fixes.

## Performance Improvements

- Optimized Nether Portal searching to remove all lag (aside from world loading)
- Optimized Redstone Dust performance to be ~17x faster

## Features

- Added Gamerule to disable minimum mob spawning distance
- Added Gamerule to disable Observers initially pulsing when placed by a player
- Added utility to track and measure spawn rates
- Added utility to asynchronously scan the world for Nether Portals
- Regenerate world heightmaps (useful when using [FAWE](https://github.com/boy0001/FastAsyncWorldedit))
- Restored vanilla spawning mechanics modified by Forge

## Bug Fixes

- Fixed mobs spawning outside of player spawn radius, causing them to despawn in the next tick
- Fixed Zombie Pigmen spawning in chunkloaded overworld portals when a player is out of range
- Fixed Redstone Torches incorrectly scheduling updates [MC-2340](https://bugs.mojang.com/browse/MC-2340)
- Fixed ghost blocks caused by pistons and mining

# How to install Forge: For Players

Go to [the Forge website](http://files.minecraftforge.net)
 and select the minecraft version you wish to get forge for from the list.

You can download the installer for the *Recommended Build* or the
 *Latest build* there. Latest builds may have newer features but may be
 more unstable as a result. The installer will attempt to install forge
 into your vanilla launcher environment, where you can then create a new
 profile using that version and play the game!

For support and questions, visit [the Support Forum](http://www.minecraftforge.net/forum/forum/18-support-bug-reports/).

Here is a short video from Rorax showing how to install and setup Forge:

[![HOWTO Install Forge](https://img.youtube.com/vi/lB3ArN_-3Oc/0.jpg)](https://www.youtube.com/watch?v=lB3ArN_-3Oc)

# How to install Forge: For Modders

[See the "Getting Started" section in the Forge Documentation](http://mcforge.readthedocs.io/en/latest/gettingstarted/).

# How to install Forge: For those wishing to work on Forge itself

If you wish to actually inspect Forge, submit PRs or otherwise work
 with Forge itself, you're in the right place!

 [See the guide to setting up a Forge workspace](http://mcforge.readthedocs.io/en/latest/forgedev/).

## Pull requests

[See the "Making Changes and Pull Requests" section in the Forge documentation](http://mcforge.readthedocs.io/en/latest/forgedev/#making-changes-and-pull-requests).

### Contributor License Agreement
We require all contributors to acknowledge the [Forge Contributor
 License Agreement](https://cla-assistant.io/MinecraftForge/MinecraftForge). Please ensure you have a valid email address
 associated with your github account to do this. If you have previously
 signed it, you should be OK.
