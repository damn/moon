(ns game.ctx.create-skin
  (:require [com.badlogic.gdx.files :as files]
            [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin]))

(defn create-skin
  [{:keys [ctx/files]} path]
  (let [skin (skin/create (files/internal files path))]
    (set! (.markupEnabled (-> skin
                              (skin/font "default-font")
                              .getData))
          true)
    skin))
