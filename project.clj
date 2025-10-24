(defproject cdq "-SNAPSHOT"
  :repositories [["jitpack" "https://jitpack.io"]]
  :dependencies [
                 [com.github.cdorrat/reduce-fsm "fe1c914d68"]

                 [com.github.damn/clojure.gdx "88092e50cc"]
                 [com.github.damn/clojure.gdx.backends.lwjgl "482fadc0f9"]
                 [com.github.damn/clojure.gdx.graphics.g2d.freetype "1bc8c46339"]
                 [com.github.damn/clojure.gdx.graphics.g2d.shape-drawer "bd69dc5369"]
                 [com.github.damn/clojure.vis-ui "41ed3ee736"]

                 [com.github.damn/clojure.grid2d "538fc4c44b"]

                 [com.github.damn/clojure.math.raycaster "0956fc0e9b"]
                 [com.github.damn/clojure.math.vector2 "9b3fd73f9b"]

                 [com.github.damn/clojure.rand "6a273c942b"]

                 [com.github.damn/malli.utils "5da493efcb"]

                 [fr.reuz/qrecord "0.1.0"]

                 [org.clj-commons/pretty "3.2.0"]

                 [org.clojure/clojure "1.12.0"]

                 ; dev-only
                 [nrepl "0.9.0"]
                 [org.clojure/tools.namespace "1.3.0"]
                 [lein-hiera "2.0.0"]
                 [com.github.damn/clojure.dev-loop "ef54a03"]
                 ;
                 ]
  :java-source-paths ["src"]
  :aliases {"dev"      ["run" "-m" "clojure.dev-loop" "((requiring-resolve 'cdq.application/-main))"]
            "levelgen" ["run" "-m" "clojure.dev-loop" "((requiring-resolve 'cdq.levelgen/-main))"]
            "nsgraph"  ["run" "-m" "clojure.dev-loop" "((requiring-resolve 'ns-graph.core/-main))"]
            "ns"       ["hiera" ":layout" ":horizontal"]}
  :plugins [[lein-hiera "2.0.0"]
            [lein-codox "0.10.8"]]
  :target-path "target/%s/" ; https://stackoverflow.com/questions/44246924/clojure-tools-namespace-refresh-fails-with-no-namespace-foo
  :jvm-opts ["-Xms512m" ; 256 for game ok, lein hiera in repl needs more
             "-Xmx512m"
             "-Dvisualvm.display.name=CDQ"
             "-XX:-OmitStackTraceInFastThrow" ; disappeared stacktraces
             ; for visualvm profiling
             ;"-Dcom.sun.management.jmxremote=true"
             ;"-Dcom.sun.management.jmxremote.port=20000"
             ;"-Dcom.sun.management.jmxremote.ssl=false"
             ;"-Dcom.sun.management.jmxremote.authenticate=false"
             ]
  :codox {:source-uri "https://github.com/damn/cdq/blob/main/{filepath}#L{line}"
          :metadata {:doc/format :markdown}}
  ; lein hiera :layout :horizontal :ignore "#{cdq.render}"
  ; unfortunately cannot exclude only 'cdq.render.*' , would like to do for entity/effect...
  ; this from engine, what purpose?
  ;:javac-options ["-target" "1.7" "-source" "1.7" "-Xlint:-options"]
  :global-vars {*warn-on-reflection* true
                ;*unchecked-math* :warn-on-boxed
                ;*assert* false
                *print-level* 3}
  :profiles {:uberjar {:aot [cdq.application]}}
  :uberjar-name "cdq.jar"
  :main cdq.application)

; * Notes

; * openjdk@8 stops working with long error
; * fireplace 'cp' evaluation does not work with openJDK17
; * using openjdk@11 right now and it works.
; -> report to vim fireplace?

; :FireplaceConnect 7888
