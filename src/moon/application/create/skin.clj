(ns moon.application.create.skin
  (:require [com.badlogic.gdx.graphics.g2d.bitmap-font :as font]
            [com.badlogic.gdx.graphics.g2d.bitmap-font.data :as font.data]
            [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin])
  (:import (com.badlogic.gdx Application)))

(defn step
  [{:keys [ctx/app]
    :as ctx}]
  (assoc ctx :ctx/skin (let [skin (skin/create (.internal (.getFiles app) "uiskin.json"))]
                         (-> skin
                             (skin/font "default-font")
                             font/data
                             (font.data/enable-markup! true))
                         skin)))
