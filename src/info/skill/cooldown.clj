(ns info.skill.cooldown
  (:require [moon.number :as number]))

(defn f [v _ctx]
  (str "Cooldown: " (number/readable v) " seconds"))
