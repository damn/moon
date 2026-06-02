(ns clojure.core.edn-str)

(defn ->edn-str ^String [v]
  (binding [*print-level* nil]
    (pr-str v)))
