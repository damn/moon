(ns moon.edn)

(defn ->str ^String [v]
  (binding [*print-level* nil]
    (pr-str v)))
