(ns moon.string
  (:require [clojure.string :as str]))

(defn remove-newlines [s]
  (let [new-s (-> s
                  (str/replace "\n\n" "\n")
                  (str/replace #"^\n" "")
                  str/trim-newline)]
    (if (= (count new-s) (count s))
      s
      (remove-newlines new-s))))

(defn truncate ^String [s limit]
  (if (> (count s) limit)
    (str (subs s 0 limit) "...")
    s))
