(ns moon.application.create.skin
  (:require [moon.files :as files]
            [com.badlogic.gdx.graphics.g2d.bitmap-font :as font]
            [com.badlogic.gdx.graphics.g2d.bitmap-font.data :as font.data]
            [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin]))

(defn step
  [ctx]
  (assoc ctx :ctx/skin (let [skin (skin/create (files/internal ctx "uiskin.json"))]
                         (-> skin
                             (skin/font "default-font")
                             font/data
                             (font.data/enable-markup! true))
                         skin)))
