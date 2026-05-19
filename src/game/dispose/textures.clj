(ns game.dispose.textures
  (:require [gdl.utils.disposable :refer [dispose!]]))

(defn do!
  [{:keys [ctx/textures]}]
  (run! dispose! (vals textures)))
