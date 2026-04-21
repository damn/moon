(ns moon.impl.skin
  (:require [clojure.files :as files]
            [clojure.gdx.scene2d.ui.skin :as skin]
            [clojure.graphics.bitmap-font :as bitmap-font]))

(defn create [{:keys [ctx/files]} path]
  (let [skin (skin/create (files/internal files path))]
    (bitmap-font/enable-markup! (skin/font skin "default-font") true)
    skin))
