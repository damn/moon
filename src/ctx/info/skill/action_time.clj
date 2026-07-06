(ns ctx.info.skill.action-time
  (:require [clojure.readable :as readable]))

(defn f [v _ctx]
  (str "Action-Time: " (readable/f v) " seconds"))
