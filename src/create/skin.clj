(ns create.skin
  (:require [game.app :as app]))

(defn step
  [{:keys [ctx/app]
    :as ctx}]
  (assoc ctx :ctx/skin (app/skin app "uiskin.json")))
