(ns clojure.moon.create-skin
  (:require [gdl.files :as files]
            [gdl.graphics.g2d.bitmap-font :as bitmap-font]
            [gdl.graphics.g2d.bitmap-font.bitmap-font-data :as bitmap-font-data]
            [gdl.scenes.scene2d.ui.skin :as skin]))

(defn f [{:keys [ctx/files] :as ctx}]
  (let [skin (skin/new (files/internal files "skin/uiskin.json"))]
    (-> skin
        (skin/get-font "default-font")
        bitmap-font/get-data
        (bitmap-font-data/set-markup-enabled! true))
    (assoc ctx :ctx/skin skin)))
