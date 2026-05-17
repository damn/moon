(ns game.dispose.textures
  (:require [clojure.gdx.utils.disposable :refer [dispose!]]))

(defn do!
  [{:keys [ctx/textures]}]
  (run! dispose! (vals textures)))
