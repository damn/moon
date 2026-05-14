(ns clojure.core-ext)

(defn require-namespaces! [namespaces]
  (run! require namespaces))
