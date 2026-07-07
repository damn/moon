(ns clojure.vector)

(defn f [[_ & params] _]
  (apply vector :vector params))
