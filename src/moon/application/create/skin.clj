(ns moon.application.create.skin
  (:require [gdl.files :as files]
            [gdl.ui.skin :as skin]
            [moon.ui]))

(defn step [{:keys [ctx/files] :as ctx}]

  ; (-> (vis-ui/skin) (skin/font "default-font") bitmap-font/data (bmfont-data/set-enable-markup! true)

  ; TODO DISPOSE

  (let [skin (skin/create (files/internal files "uiskin.json"))]
    (.bindRoot #'moon.ui/skin skin)
    (assoc ctx :ctx/skin skin)))
