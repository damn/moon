(ns moon.create.skin
  (:require [clojure.files :as files]
            [clojure.gdx.scene2d.ui.skin :as skin]
            [clojure.graphics.bitmap-font :as bitmap-font]))

(defn step [{:keys [ctx/files] :as ctx} path]
  (assoc ctx :ctx/skin (let [skin (skin/create (files/internal files "uiskin.json"))]
                         (bitmap-font/enable-markup! (skin/font skin "default-font") true)
                         skin)))
