(ns loop
  (:require [clj-commons.pretty.repl]
            [clojure.java.io]
            [clojure.tools.namespace.repl]
            [nrepl.server]))

(clojure.tools.namespace.repl/disable-reload!) ; keep same connection/nrepl-server up throughout refreshs

(def ^Object obj (Object.))

(def thrown (atom false))

(defn handle-throwable! [t]
  (binding [*print-level* 3]
    (clj-commons.pretty.repl/pretty-pst t))
  (reset! thrown t))

(defn restart!
  "Calls refresh on all namespaces with file changes and restarts the application."
  []
  (if @thrown
    (do
     (reset! thrown false)
     (locking obj
       (println "\n\n>>> RESTARTING <<<")
       (.notify obj)))
    (println "\n Application still running! Cannot restart.")))

(declare refresh-error)

(declare start-app-expression)

(defn start-dev-loop! []
  (try (eval start-app-expression)
       (catch Throwable t
         (handle-throwable! t)))
  (loop []
    (when-not @thrown
      (do
       (.bindRoot #'refresh-error (clojure.tools.namespace.repl/refresh :after 'loop/start-dev-loop!))
       (handle-throwable! refresh-error)))
    (locking obj
      (Thread/sleep 10)
      (println "\n\n>>> WAITING FOR RESTART <<<")
      (.wait obj))
    (recur)))

(defn -main [start-expression]
  (.bindRoot #'start-app-expression (read-string start-expression))
  (let [nrepl-server (nrepl.server/start-server)
        port-file (clojure.java.io/file ".nrepl-port")]
    ;; Many clients look for this file to infer the port to connect to
    (.deleteOnExit ^java.io.File port-file)
    (spit port-file (:port nrepl-server))
    (println "Started nrepl server on port" (:port nrepl-server))
    (start-dev-loop!)))
