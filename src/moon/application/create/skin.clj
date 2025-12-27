(ns moon.application.create.skin
  (:require [gdl.files :as files]
            [gdl.graphics.bitmap-font :as bitmap-font]
            [gdl.graphics.bitmap-font.data :as bitmap-font-data]
            [gdl.ui.skin :as skin]
            [moon.ui]))

(defn step [{:keys [ctx/files] :as ctx}]
  (let [skin (skin/create (files/internal files "uiskin.json"))]
    (-> skin
        (skin/font "default-font")
        bitmap-font/data
        (bitmap-font-data/set-enable-markup! true))
    (.bindRoot #'moon.ui/skin skin)
    (assoc ctx :ctx/skin skin)))
