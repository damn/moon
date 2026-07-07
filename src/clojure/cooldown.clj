(ns clojure.cooldown
  (:require [clojure.readable :as readable]))

(defn f [v _ctx]
  (str "Cooldown: " (readable/f v) " seconds"))
