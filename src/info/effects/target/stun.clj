(ns info.effects.target.stun
  (:require [moon.number :as number]))

(defn f [duration _ctx]
  (str "Stuns for " (number/readable duration) " seconds"))
