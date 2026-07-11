(defproject moon "-SNAPSHOT"
  :repositories [["jitpack" "https://jitpack.io"]]
  :dependencies [
                 [com.github.damn/com.badlogic.gdx "361519e8e3"]
                 [com.github.damn/com.badlogic.gdx.backends.lwjgl3 "5042308fb0"]
                 [com.github.damn/com.badlogic.gdx.graphics.g2d.freetype "38124e642d"]
                 [com.badlogicgames.gdx/gdx-freetype-platform "1.14.2" :classifier "natives-desktop"]
                 [com.github.damn/space.earlygrey.shapedrawer "1184b47b65"]
                 [com.github.cdorrat/reduce-fsm "fe1c914d68"]
                 [fr.reuz/qrecord "0.1.0"]
                 [metosin/malli "0.13.0"]
                 [org.clj-commons/pretty "3.2.0"]
                 [org.clojure/clojure "1.12.0"]
                 ; development only
                 [nrepl "0.9.0"]
                 [org.clojure/tools.namespace "1.3.0"]
                 ]
  :java-source-paths ["java-src"]
  :source-paths ["src"]
  :resource-paths ["resources/"]
  :aliases {
            "dev"      ["run" "-m" "moon.loop" "((requiring-resolve 'moon.game/-main))"]
            "levelgen" ["run" "-m" "moon.loop" "((requiring-resolve 'moon.levelgen/-main))"]
            "editor"   ["run" "-m" "moon.loop" "((requiring-resolve 'moon.editor/-main))"]
            }
  :plugins [[lein-hiera "2.0.0"]
            [lein-codox "0.10.8"]]
  :target-path "target/%s/" ; https://stackoverflow.com/questions/44246924/clojure-tools-namespace-refresh-fails-with-no-namespace-foo
  :jvm-opts ["-Xms512m" ; 256 for game ok, lein hiera in repl needs more
             "-Xmx512m"
             "-Dvisualvm.display.name=CDQ"
             "-XX:-OmitStackTraceInFastThrow" ; disappeared stacktraces
             ]
  :codox {:source-uri "https://github.com/damn/moon/blob/main/{filepath}#L{line}"
          :metadata {:doc/format :markdown}}
  :global-vars {*warn-on-reflection* true
                ;*unchecked-math* :warn-on-boxed
                ;*assert* false
                *print-level* 3}
  :profiles {:uberjar {:aot [moon.game]}}
  :uberjar-name "moon.jar"
  :main moon.game)
