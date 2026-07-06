(ns ctx.info.effects.target.stun
  (:require [clojure.readable :as readable]))

(defn f [duration _ctx]
  (str "Stuns for " (readable/f duration) " seconds"))
