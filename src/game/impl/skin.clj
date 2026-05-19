(ns game.impl.skin
  (:require [gdl.app :as app]
            [gdl.files :as files]
            [gdl.files.file-handle :as file-handle]
            [gdl.graphics.g2d.bitmap-font :as font]
            [gdl.graphics.g2d.bitmap-font.data :as font.data]
            [gdl.scene2d.ui.skin :as skin]))

(defn create
  [{:keys [ctx/app]}]
  (let [skin (file-handle/skin (files/internal (app/files app) "uiskin.json"))]
    (-> skin
        (skin/font "default-font")
        font/data
        (font.data/set-markup-enabled! true))
    skin))
