(ns clojure.dev-loop
  (:require clj-commons.pretty.repl
            clojure.java.io
            clojure.tools.namespace.repl
            nrepl.server))

(clojure.tools.namespace.repl/disable-reload!) ; keep same connection/nrepl-server up throughout refreshs

(def ^:private ^Object obj (Object.))

(def ^:private thrown (atom false))

(defn- handle-throwable! [t]
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

(declare ^:private refresh-error)

(declare ^:no-doc start-app-expression)

(defn ^:no-doc start-dev-loop! []
  (try (eval start-app-expression)
       (catch Throwable t
         (handle-throwable! t)))
  (loop []
    (when-not @thrown
      (do
       (.bindRoot #'refresh-error (clojure.tools.namespace.repl/refresh :after 'clojure.dev-loop/start-dev-loop!))
       (handle-throwable! refresh-error)))
    (locking obj
      (Thread/sleep 10)
      (println "\n\n>>> WAITING FOR RESTART <<<")
      (.wait obj))
    (recur)))

; ( I dont know why nrepl start-server does not have this included ... )
(defn- save-port-file!
  "Writes a file relative to project classpath with port number so other tools
  can infer the nREPL server port.
  Takes nREPL server map and processed CLI options map.
  Returns nil."
  [server]
  ;; Many clients look for this file to infer the port to connect to
  (let [port (:port server)
        port-file (clojure.java.io/file ".nrepl-port")]
    (.deleteOnExit ^java.io.File port-file)
    (spit port-file port)))

(declare ^:private nrepl-server)

(defn -main [start-expression]
  (.bindRoot #'start-app-expression (read-string start-expression))
  (.bindRoot #'nrepl-server (nrepl.server/start-server))
  (save-port-file! nrepl-server)
  (println "Started nrepl server on port" (:port nrepl-server))
  (start-dev-loop!))
