(ns clojure.gdx.skin
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.skin :as skin]
            [gdl.bitmap-font :as bitmap-font]))

(defn create [file-handle]
  (let [skin (skin/create file-handle)]
    (bitmap-font/enable-markup! (skin/font skin "default-font") true)
    skin))
