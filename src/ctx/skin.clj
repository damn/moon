(ns ctx.skin
  (:require
            [com.badlogic.gdx.graphics.g2d.bitmap-font$bitmap-font-data :as bitmap-font-data]
            [com.badlogic.gdx.graphics.g2d.bitmap-font :as bitmap-font]
            [com.badlogic.gdx.files :as files]
            [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin]))

(defn step
  [{:keys [ctx/files]}]
  (let [skin (skin/new (files/internal files "skin/uiskin.json"))]
    (-> skin
        (skin/get-font "default-font")
        bitmap-font/get-data
        (bitmap-font-data/set-markup-enabled! true))
    skin))
