(ns game.dispose.audio
  (:require [gdl.utils.disposable :refer [dispose!]]))

(defn do!
  [{:keys [ctx/audio]}]
  (run! dispose! (vals audio)))
