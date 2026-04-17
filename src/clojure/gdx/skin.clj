(ns clojure.gdx.skin
  (:require [clojure.gdx.scene2d.ui.skin :as skin]
            [clojure.bitmap-font :as bitmap-font]))

(defn create [file-handle]
  (let [skin (skin/create file-handle)]
    (bitmap-font/enable-markup! (skin/font skin "default-font") true)
    skin))
