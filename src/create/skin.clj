(ns create.skin
  (:require [com.badlogic.gdx.files :as files]
            [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin]
            [com.badlogic.gdx.graphics.g2d.bitmap-font.enable-markup :as enable-markup]))

(defn step
  [{:keys [ctx/files]}]
  (let [skin (skin/create (files/internal files "skin/uiskin.json"))]
    (-> skin
        (skin/font "default-font")
        enable-markup/f!)
    skin))
