**Overview**: Overhaul the existing linear in game tutorials to a quest system.

**Task breakdown**

- Design a schema for quests  
- Design a file format for quests  
- Design gameplay progress functions that allow quests to check that a user has completed a specific action

**Anatomy of a Quest**  
**Anatomy of a Quest**  
Quests introduce a split of the tutorial into a collection of individual linear pathways that can be followed independently and out-of-order as a user often decides their own playstyle which may not include every part of the game. (ie. a player may stick to island and never go to the adventure, a player may also only go to adventure and never interact with island)

**Design Goals**

- Allow users to choose their own pathway  
- Possible to live-update  
  - can be changed without changing any code  
- Stay with you throughout the entire season  
  - the quest line will span the entire season of gameplay

**Renderer Implementation**

- Initial Prototype (Complete)  
  - Chat Based renderer  
- Next Milestone  
  - New Renderer, options:  
    - GUI Based renderer  
- Texture Pack based renderer that displays the text description of your active quest on your screen at all times

**Quest Storage Implementation**

- Initial Prototype (Complete)  
  - A single file describes the entire quest system  
- Next Milestone  
  - Allow hot swapping quests without server restart  
  - Show any changes to the quests to make explicit what changes will be committed and then require a confirmation afterward to ensure no unwanted changes are committed  
  - To update to a new file, the new file is dropped in a drop folder  
    - An admin then runs \`/checkquests {filename}\`  
    - An admin then replaces the existing quest file with the new quest file during server downtime

**Next Quest Suggestion**

- Next Milestone  
  - Allow quests to list a next quest (or many)  
    - Interface with existing systems of the server to ensure quests can be completed and are not locked behind level cap/fund

**Potential Pitfalls / Need more exploration**

- How to backfill quest completions for quests added after a season has already started  
- A translation system / localization for non-english languages  
- How to prevent layered quests (ie. bandit kills 1,2,3) from having one account complete the quests for the entire group to maximize rewards?  
- Have analytics for admins of quest completions / have command to list quests that have least completions

**Considerations**

- Follow up quests should be better than first quest in terms of completion to rewards to disincentivize using alt accounts to complete the first quest of the questline many times  
- purposefully do not have prerequisites on start of quest line to prevent requiring unrelated quest completions  
- Idea: show next 2 locked quest rewards in a quest line but avoid showing all to incentivize exploring but not too far from current progress  
- Programmatically generate quest descriptions?  
  - Currently done to reduce effort when making quests, but could have an option to have a manually-enterable description.  
- Should the claimed rewards show what it took to claim them?  
  - Not done now as text interface is only for initial prototype  
- QuestLine descriptions are currently not shown in the text-based UI but should be shown in the item-based GUI

**Admin Notes**

- Quests **MUST** not have a semicolon in their id  
- Quest condition progress is stored in nbt, current values can be found using the command \`/data get entity \<username\>\`, scrolling to the top, and seeing the ”BukkitValues:” text, inside the curly braces are the current values for that player.  
  - Completed quests are quests whose conditions are completed  
  - Claimed reward quests are quests whose quest rewards have been claimed  
  - Anything that has a value that ends in L is a number  
  - Anything that has a prefix of “quest\_system:” belongs to this plugin  
  - Anything that has a suffix of “\_requirement” means it’s a temporary value representing the lowest requirement for that goal  
    - This is a caching condition so we don’t check every goal every time, we only check goals once the condition being updated reaches this value, you probably won’t ever need to worry about this in game