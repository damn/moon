(defproject moon "-SNAPSHOT"
  :repositories [["jitpack" "https://jitpack.io"]]
  :dependencies [
                 [com.github.damn/clojure.core-ext "55983eb35e"]
                 [com.github.damn/clojure.math "8c8d074607"]
                 [com.github.damn/clojure.rand "8b08b1e70d"]
                 [com.github.damn/com.badlogic.gdx "e75c53c521"]
                 [com.github.damn/com.badlogic.gdx.backends.lwjgl3 "01329de8e4"]
                 [com.github.damn/com.badlogic.gdx.graphics.g2d.freetype "29d1626eca"]
                 [com.github.damn/space.earlygrey.shape-drawer "82d772e482"]
                 [com.github.damn/malli.utils "081a715935"]

                 [com.github.cdorrat/reduce-fsm "fe1c914d68"]
                 [fr.reuz/qrecord "0.1.0"]
                 [org.clj-commons/pretty "3.2.0"]
                 [org.clojure/clojure "1.12.0"]

                 ; DEV:
                 [nrepl "0.9.0"]
                 [org.clojure/tools.namespace "1.3.0"]

                 ; Unused:
                 [lein-hiera "2.0.0"]
                 ]
  :java-source-paths ["java-src"]
  :source-paths ["src"]
  :resource-paths ["resources/"]
  :aliases {"dev"      ["run" "-m" "dev.loop" "((requiring-resolve 'start/-main))"]
            "levelgen" ["run" "-m" "dev.loop" "((requiring-resolve 'moon.levelgen/-main))"]
            "editor"   ["run" "-m" "dev.loop" "((requiring-resolve 'editor.app/-main))"]
            "nsgraph"  ["run" "-m" "dev.loop" "((requiring-resolve 'ns-graph.core/-main))"]
            "app-test" ["run" "-m" "dev.loop" "((requiring-resolve 'moon.backends.lwjgl-test/-main))"]
            "ns"       ["hiera" ":layout" ":horizontal"]}
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
  :profiles {:uberjar {:aot [start]}}
  :uberjar-name "moon.jar"
  :main start)
