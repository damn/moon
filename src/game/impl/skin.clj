(ns game.impl.skin
  (:require [clojure.gdx.app :as app]
            [clojure.gdx.files :as files]
            [com.badlogic.gdx.graphics.g2d.bitmap-font :as font]
            [com.badlogic.gdx.graphics.g2d.bitmap-font.data :as font.data]
            [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin]))

(defn create
  [{:keys [ctx/app]}]
  (let [skin (skin/create (files/internal (app/files app) "uiskin.json"))]
    (-> skin
        (skin/font "default-font")
        font/data
        (font.data/set-markup-enabled! true))
    skin))
