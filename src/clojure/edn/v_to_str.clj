(ns clojure.edn.v-to-str)

(defn ->edn-str ^String [v]
  (binding [*print-level* nil]
    (pr-str v)))
