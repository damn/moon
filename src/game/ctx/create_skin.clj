(ns game.ctx.create-skin
  (:require [com.badlogic.gdx.files :as files]
            [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin]
            [com.badlogic.gdx.graphics.g2d.bitmap-font.enable-markup :as enable-markup]))

(defn create-skin
  [{:keys [ctx/files]} path]
  (let [skin (skin/create (files/internal files path))]
    (-> skin
        (skin/font "default-font")
        enable-markup/f!)
    skin))
