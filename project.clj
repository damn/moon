(defproject moon "-SNAPSHOT"
  :repositories [["jitpack" "https://jitpack.io"]]
  :dependencies [
                 [com.github.damn/com.badlogic.gdx "91ae1306879264ecfbe9239d5e570d054899077d"]

                 [com.badlogicgames.gdx/gdx-backend-lwjgl3    "1.14.2"]

                 [com.badlogicgames.gdx/gdx-freetype          "1.14.2"]
                 [com.badlogicgames.gdx/gdx-freetype-platform "1.14.2" :classifier "natives-desktop"]

                 [com.badlogicgames.gdx/gdx-platform          "1.14.2" :classifier "natives-desktop"]

                 [space.earlygrey/shapedrawer "2.6.0"]
                 ;

                 ; clojure libs
                 [com.github.cdorrat/reduce-fsm "fe1c914d68"]
                 [fr.reuz/qrecord "0.1.0"]
                 [metosin/malli "0.13.0"]
                 [org.clj-commons/pretty "3.2.0"]
                 ;

                 ; clojure
                 [org.clojure/clojure "1.12.0"]
                 ;

                 ; development only
                 [nrepl "0.9.0"]
                 [org.clojure/tools.namespace "1.3.0"]
                 ;
                 ]
  :java-source-paths ["java-src"]
  :source-paths ["src"]
  :resource-paths ["resources/"]
  :aliases {
            "dev"      ["run" "-m" "clojure.loop" "((requiring-resolve 'clojure.moon/-main))"]
            "levelgen" ["run" "-m" "clojure.loop" "((requiring-resolve 'clojure.levelgen-test/-main))"]
            "editor"   ["run" "-m" "clojure.loop" "((requiring-resolve 'clojure.editor/-main))"]
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
  :profiles {:uberjar {:aot [clojure.moon]}}
  :uberjar-name "moon.jar"
  :main clojure.moon)
