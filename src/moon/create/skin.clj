(ns moon.create.skin
  (:require [clojure.gdx.scene2d.ui.skin :as skin]
            [clojure.graphics.bitmap-font :as bitmap-font])
  (:import (com.badlogic.gdx Files)))

(defn step
  [{:keys [ctx/files]
    :as ctx}]
  (assoc ctx :ctx/skin (let [skin (skin/create (Files/.internal files "uiskin.json"))]
                         (bitmap-font/enable-markup! (skin/font skin "default-font") true)
                         skin)))
