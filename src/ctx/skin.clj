(ns ctx.skin
  (:require [clojure.gdx :as gdx]))

(defn step
  [{:keys [ctx/files]}]
  (let [skin (gdx/skin (gdx/internal files "skin/uiskin.json"))]
    (gdx/font-set-markup-enabled! (gdx/skin-get-font skin "default-font") true)
    skin))
