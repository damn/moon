(ns info.skill.action-time
  (:require [moon.number :as number]))

(defn f [v _ctx]
  (str "Action-Time: " (number/readable v) " seconds"))
