(ns create.skin
  (:require [gdx.app :as app]))

(defn step
  [{:keys [ctx/app]
    :as ctx}]
  (assoc ctx :ctx/skin (app/skin app "skin/uiskin.json")))
