# What is the core of the game? the world/board/model - data/state-space?

What is what? names/references/code reader document browser project tool shows green manual/automated tests passed, git status, next tasks, ?
open code, highlight names, usage==..! what is what meaning (ctx) ?
where all is 'ctx' used ? only application should be




# How is all connected? Is it all required? Make another game same project? monorepo,
INTERNAL ONLY REPO, ... ? for yourself.

resources/ -> ok
resoures/cursors/ ->

Language is the problem


0.
The game is what it is, remove all issues/documents otherwise you want to fix it now.
What are the building blocks, (libraries?), game-domain-data?, schema world/entity, ?
Namespaces? Tests? Documentation? Deployment?

-> Remove all comments/dead-code/documentation/etc.
-> Let it be what it is. Readme just: lein namespace graph, project?, idk, commands, tests running
(manual tests checkbox green?) , project status?, donations itch.io release?
youtube video/stream/etc?

1.
resources/cursors/

    -> why so many cursors?
    -> many 'states' which the mouse can be?
    -> What are those game-states? What is the ctx/world/entity/etc. schema?
    -> set-cursor knows about world internals & state dispatch -> state separate component....
    -> what are the game/interaction rules? what can happen, what is displayed?
    -> the right way to look at it is required. Then you will understand
    -> What is there, how is it connected, what changes and what not, what is the 'state-space' of the application/game. -> gdx is more application specific, does not influence world/board/game-rules.
    -> separate presentation from game-rules/game-data.
    -> Start with the data? 2D PvP turn-based, tile-based, inventory, start with resources ultimate fantasy tileset?
