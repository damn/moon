
# Moon Engine

![moon 2](https://github.com/user-attachments/assets/b1c666de-2aad-427f-b06a-c838048958bb)

Moon Engine is an open-source Action RPG-Maker written in [clojure](https://clojure.org/) and utilizing [libgdx](https://libgdx.com/) as a `backend`.

It supports a property-editor for all game relevant components like audiovisuals, creatures, items, skills, and ... effects/modifiers.

Everything is stored in a 'DATABASE' where stuff can be edited.... => So why not make it a separate project the 'moon.db' project?

## Video

[![YouTube](http://i.ytimg.com/vi/FJC_aCXQv58/hqdefault.jpg)](https://www.youtube.com/watch?v=FJC_aCXQv58)

## Screenshots

<details>
  <summary>Ingame</summary>
  <img width="1440" alt="Screenshot 2024-10-03 at 12 08 30 PM" src="https://github.com/user-attachments/assets/6780f0c4-0729-46ca-b254-a1816af2d6c8">
</details>

<details>
  <summary>Editor</summary>
<img width="750" alt="reddragoneditor" src="https://github.com/user-attachments/assets/62b91a90-784e-4948-a89f-7d18fefce142">


</details>

## Levels

Levels can be created with [tiled](https://www.mapeditor.org/) or procedurally or mixed with the use of modules. There is one example world for each approach.

<details>
  <summary>Screenshot</summary>
  <img width="1440" alt="Screenshot 2024-10-07 at 6 22 54 PM" src="https://github.com/user-attachments/assets/a59d276b-ab6b-4a28-a392-5aa62823d6f8">

</details>

## How to start

You need to have [leiningen](https://leiningen.org/) installed.

```
lein dev
```

<details>
  <summary>Dev-loop contains:</summary>

* NREPL-Server
* On application close (ESC in the main menu), clojure.tools.namespace will do  refresh on any changed files and restart the app.
* On any error the JVM does not have to be restarted, you can fix the error and call `gdx.dev/restart!`
    * I have bound it on my VIM to F5 with: `nmap <F5> :Eval (do (in-ns 'gdx.dev)(restart!))`

</details>

## [API Docs](https://damn.github.io/moon/)

## License

* Code Licensed under MIT License

* The assets used are proprietary and not open source
    * Tilesets by https://winlu.itch.io/
    * Creatures, Items, Skill-Icons,FX and other assets by https://www.oryxdesignlab.com
    * Cursors from Leonid Deburger https://deburger.itch.io/
 
# Commands

## Sort files by lines of code

```
sh tasks/count_locs.sh
```

~result here~

## Show namespace graph

```
lein hiera :layout :horizontal
```

~ reslt here

## Show package graph

~~~
...
~~~

## Show dependency tree

## Open Structural Code Browser (instead of docs)

# TODO

* Liste dependencies with __javadoc io__ (!!!) & github (e.g. kotcrab, javadoc.io libgdx & wiki .. )
    => hidden in my browser.
    => my bookmarks.

        -> lwjgl javadoc not needed as we use libgdx
        -> libgdx javadoc not needed as we use 'core' ?
        -> ... ?

* greppable click where used searching right files/folders ?

* show also import dependencies in dependency graph & assets/edn files

* count locs
 find clj/ -name '*.clj' | xargs wc -l | sort -n

    * The font exocet is open source

