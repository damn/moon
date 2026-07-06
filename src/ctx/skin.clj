(ns ctx.skin
  (:require [clojure.gdx.bitmap-font.get-data :as get-data]
            [clojure.gdx.bitmap-font$bitmap-font-data.set-markup-enabled :as set-markup-enabled]
            [com.badlogic.gdx.files :as files]
            [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin]))

(defn step
  [{:keys [ctx/files]}]
  (let [skin (skin/new (files/internal files "skin/uiskin.json"))]
    (-> skin
        (skin/get-font "default-font")
        get-data/f
        (set-markup-enabled/f true))
    skin))
