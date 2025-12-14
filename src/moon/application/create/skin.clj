(ns moon.application.create.skin
  (:require [gdl.ui :as ui]
            [moon.ui]))

(defn step [{:keys [ctx/app] :as ctx}]
  ; (-> (vis-ui/skin) (skin/font "default-font") bitmap-font/data (bmfont-data/set-enable-markup! true)
  ; TODO DISPOSE
  (let [skin (ui/skin (.internal (.getFiles app) "uiskin.json"))]
    (.bindRoot #'moon.ui/skin skin)
    (assoc ctx :ctx/skin skin)))
