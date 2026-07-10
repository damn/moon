(ns clojure.moon.create-skin
  (:require [clojure.files :as files]
            [gdl.bitmap-font :as bitmap-font]
            [gdl.bitmap-font.data :as bitmap-font-data]
            [clojure.skin :as skin]))

(defn f [{:keys [ctx/files] :as ctx}]
  (let [skin (skin/new (files/internal files "skin/uiskin.json"))]
    (-> skin
        (skin/get-font "default-font")
        bitmap-font/get-data
        (bitmap-font-data/set-markup-enabled! true))
    (assoc ctx :ctx/skin skin)))
