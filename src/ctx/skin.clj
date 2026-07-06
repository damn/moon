(ns ctx.skin
  (:require
            [com.badlogic.gdx.graphics.g2d.bitmap-font :as bitmap-font]
            [clojure.gdx.bitmap-font$bitmap-font-data.set-markup-enabled :as set-markup-enabled]
            [com.badlogic.gdx.files :as files]
            [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin]))

(defn step
  [{:keys [ctx/files]}]
  (let [skin (skin/new (files/internal files "skin/uiskin.json"))]
    (-> skin
        (skin/get-font "default-font")
        bitmap-font/get-data
        (set-markup-enabled/f true))
    skin))
