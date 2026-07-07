(ns clojure.truncate)

(defn truncate ^String [s limit]
  (if (> (count s) limit)
    (str (subs s 0 limit) "...")
    s))
