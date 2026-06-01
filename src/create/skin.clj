(ns create.skin
  (:require [clojure.gdx.application :as app]
            [com.badlogic.gdx.files :as files]
            [clojure.gdx.graphics.g2d.bitmap-font :as font]
            [clojure.gdx.graphics.g2d.bitmap-font.data :as font.data]
            [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin]))

(defn step
  [{:keys [ctx/app]
    :as ctx}]
  (assoc ctx :ctx/skin
         (let [skin (skin/create (files/internal (app/files app) "skin/uiskin.json"))]
           (-> skin
               (skin/font "default-font")
               font/data
               font.data/enable-markup!)
           skin)))
