(ns clojure.qualified-keyword)

(defn f [[_ & params] _]
  (apply vector :qualified-keyword params))
