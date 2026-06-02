(ns clojure.core.require-namespaces)

(defn require-namespaces! [namespaces]
  (run! require namespaces))
