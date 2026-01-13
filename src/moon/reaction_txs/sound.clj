(ns moon.reaction-txs.sound
  (:require [moon.audio :as audio]))

(defn do!
  [{:keys [ctx/audio] :as ctx} sound-name]
  (audio/play! audio sound-name)
  ctx)
